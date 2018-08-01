package sfllhkhan95.doodle.core.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.utils.OnToolSelectedListener;

/**
 * @author saifkhichi96
 */
public class ToolboxView extends LinearLayout {

    private final ArrayList<Integer> nonSticky = new ArrayList<>();

    private final LinearLayout primaryToolbox;
    private final LinearLayout secondaryToolbox;

    private int primarySelected = -1;
    private OnToolSelectedListener toolSelectedListener;

    public ToolboxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutParams params = new LayoutParams(context, attrs);
        params.weight = 1;

        primaryToolbox = new LinearLayout(context, attrs);
        primaryToolbox.setLayoutParams(params);

        secondaryToolbox = new LinearLayout(context, attrs);
        secondaryToolbox.setLayoutParams(params);

        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(p);

        this.setOrientation(VERTICAL);
        this.setWeightSum(2);

        addView(secondaryToolbox);
        addView(primaryToolbox);
        init();

        try {
            this.setOnToolSelectedListener((OnToolSelectedListener) context);
        } catch (Exception ignored) {

        }

        secondaryToolbox.setVisibility(GONE);
    }

    public ToolboxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(context, attrs);
        params.weight = 1;

        primaryToolbox = new LinearLayout(context, attrs, defStyleAttr);
        primaryToolbox.setLayoutParams(params);

        secondaryToolbox = new LinearLayout(context, attrs, defStyleAttr);
        secondaryToolbox.setLayoutParams(params);

        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(p);

        this.setOrientation(VERTICAL);
        this.setWeightSum(2);

        addView(secondaryToolbox);
        addView(primaryToolbox);
        init();

        try {
            this.setOnToolSelectedListener((OnToolSelectedListener) context);
        } catch (Exception ignored) {

        }

        secondaryToolbox.setVisibility(GONE);
    }

    private void inflateItem(final LinearLayout root, final MenuItem item) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;

        if (item.isCheckable()) {
            nonSticky.add(item.getItemId());
        }

        LinearLayout toolView = (LinearLayout) View.inflate(getContext(), R.layout.view_tool_icon, null);
        toolView.setLayoutParams(params);

        final ImageButton itemView = toolView.findViewById(R.id.icon);
        itemView.setId(item.getItemId());
        itemView.setImageDrawable(item.getIcon());

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    if (v.getId() == ((LinearLayout) root.getChildAt(i)).getChildAt(0).getId()) {
                        if (item.hasSubMenu()) {
                            if (primarySelected != v.getId() || secondaryToolbox.getVisibility() == GONE) {
                                inflateMenu(secondaryToolbox, item.getSubMenu());
                                secondaryToolbox.setVisibility(VISIBLE);

                                selectTool(primaryToolbox, v.getId());

                                if (hasOnToolSelectedListener()) {
                                    int id = ((LinearLayout) secondaryToolbox.getChildAt(0)).getChildAt(0).getId();
                                    selectTool(secondaryToolbox, id);

                                    toolSelectedListener.onToolSelected(!nonSticky.contains(id), id);
                                }
                            } else {
                                secondaryToolbox.setVisibility(GONE);

                                if (hasOnToolSelectedListener()) {
                                    toolSelectedListener.onToolSelected(!nonSticky.contains(v.getId()), v.getId());
                                }
                            }
                        } else {
                            if (root.equals(primaryToolbox)) {
                                secondaryToolbox.setVisibility(GONE);
                                selectTool(primaryToolbox, v.getId());
                            } else {
                                selectTool(secondaryToolbox, v.getId());
                            }

                            if (hasOnToolSelectedListener()) {
                                toolSelectedListener.onToolSelected(!nonSticky.contains(v.getId()), v.getId());
                            }
                        }
                    }
                }
            }
        });

        root.addView(toolView);
    }

    private boolean hasOnToolSelectedListener() {
        return toolSelectedListener != null;
    }

    private void inflateMenu(LinearLayout root, Menu menu) {
        root.removeAllViews();

        setWeightSum(menu.size());
        for (int i = 0; i < menu.size(); i++)
            inflateItem(root, menu.getItem(i));
    }

    private void init() {
        try {
            Class<?> menuBuilderClass = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Constructor<?> constructor = menuBuilderClass.getDeclaredConstructor(Context.class);
            Menu menu = (Menu) constructor.newInstance(getContext());

            MenuInflater inflater = new MenuInflater(getContext());
            inflater.inflate(R.menu.tools, menu);

            inflateMenu(primaryToolbox, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolColor(int itemId, int color) {
        try {
            ((ImageButton) findViewById(itemId)).setColorFilter(color);
        } catch (Exception ignored) {

        }
    }

    public void selectTool(LinearLayout root, int id) {
        if (nonSticky.contains(id)) return;

        deselectAll(root);
        if (root.equals(primaryToolbox))
            primarySelected = id;

        setToolColor(id, Color.parseColor("#883997"));
    }

    public void deselectAll(LinearLayout root) {
        if (root.equals(primaryToolbox))
            primarySelected = -1;

        for (int i = 0; i < root.getChildCount(); i++) {
            setToolColor(((LinearLayout) root.getChildAt(i)).getChildAt(0).getId(), Color.WHITE);
        }
    }

    public void updateFillColorPicker(int color) {
        setToolColor(R.id.fillColorPicker, color);
    }

    public void updatePenColorPicker(int color) {
        setToolColor(R.id.penColorPicker, color);
    }

    public void setOnToolSelectedListener(OnToolSelectedListener toolSelectedListener) {
        this.toolSelectedListener = toolSelectedListener;
    }

}
