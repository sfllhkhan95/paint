package sfllhkhan95.doodle.core.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.3.0
 */
public class OptionsDialog extends Dialog {

    private ImageView dialogIcon;
    private TextView titleView;
    private TextView messageView;

    private String option1;
    private ImageView option1Icon;
    private TextView option1Label;
    private View.OnClickListener op1Listener;

    private String option2;
    private ImageView option2Icon;
    private TextView option2Label;
    private View.OnClickListener op2Listener;

    private String option3;
    private ImageView option3Icon;
    private TextView option3Label;
    private View.OnClickListener op3Listener;

    private String title;
    private String message;

    @DrawableRes
    private int iconId = -1;

    @DrawableRes
    private int iconOp1Id = -1;

    @DrawableRes
    private int iconOp2Id = -1;

    @DrawableRes
    private int iconOp3Id = -1;

    private boolean labelsEnabled = false;

    private boolean adsDisabled = true;

    private OptionsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(R.layout.dialog_options);

        // Get UI elements
        dialogIcon = findViewById(R.id.icon);
        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);

        option1Icon = findViewById(R.id.op1_icon);
        option1Label = findViewById(R.id.op1_label);

        option2Icon = findViewById(R.id.op2_icon);
        option2Label = findViewById(R.id.op2_label);

        option3Icon = findViewById(R.id.op3_icon);
        option3Label = findViewById(R.id.op3_label);

        // Configure UI elements
        if (iconId != -1) {
            dialogIcon.setImageResource(iconId);
        }
        titleView.setText(title);
        messageView.setText(message);

        findViewById(R.id.op1).setVisibility(View.GONE);
        findViewById(R.id.op2).setVisibility(View.GONE);
        findViewById(R.id.op3).setVisibility(View.GONE);

        if (iconOp1Id != -1) {
            findViewById(R.id.op1).setVisibility(View.VISIBLE);
            option1Icon.setImageResource(iconOp1Id);
            option1Label.setText(option1);

            option1Icon.setOnClickListener(op1Listener);
            option1Label.setOnClickListener(op1Listener);
        }

        if (iconOp2Id != -1) {
            findViewById(R.id.op2).setVisibility(View.VISIBLE);
            option2Icon.setImageResource(iconOp2Id);
            option2Label.setText(option2);

            option2Icon.setOnClickListener(op2Listener);
            option2Label.setOnClickListener(op2Listener);
        }

        if (iconOp3Id != -1) {
            findViewById(R.id.op3).setVisibility(View.VISIBLE);
            option3Icon.setImageResource(iconOp3Id);
            option3Label.setText(option3);

            option3Icon.setOnClickListener(op3Listener);
            option3Label.setOnClickListener(op3Listener);
        }

        if (labelsEnabled) {
            showLabels();
        } else {
            hideLabels();
        }

        // Display ads if they are enabled
        if (!adsDisabled) {
            final AdView mAdView = this.findViewById(R.id.adView);
            AdManager.loadBanner(mAdView, new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void showLabels() {
        option1Label.setVisibility(View.VISIBLE);
        option2Label.setVisibility(View.VISIBLE);
        option3Label.setVisibility(View.VISIBLE);
    }

    private void hideLabels() {
        option1Label.setVisibility(View.GONE);
        option2Label.setVisibility(View.GONE);
        option3Label.setVisibility(View.GONE);
    }

    public static class Builder {

        private final OptionsDialog optionsDialog;

        public Builder(Context context) {
            optionsDialog = new OptionsDialog(context);
        }

        public Builder setIcon(int iconId) {
            optionsDialog.iconId = iconId;
            return this;
        }

        public Builder setTitle(String title) {
            optionsDialog.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            optionsDialog.message = message;
            return this;
        }

        public Builder setOption1(String label, @DrawableRes int iconRes, View.OnClickListener clickListener) {
            optionsDialog.option1 = label;
            optionsDialog.iconOp1Id = iconRes;
            optionsDialog.op1Listener = clickListener;
            return this;
        }

        public Builder setOption2(String label, @DrawableRes int iconRes, View.OnClickListener clickListener) {
            optionsDialog.option2 = label;
            optionsDialog.iconOp2Id = iconRes;
            optionsDialog.op2Listener = clickListener;
            return this;
        }

        public Builder setOption3(String label, @DrawableRes int iconRes, View.OnClickListener clickListener) {
            optionsDialog.option3 = label;
            optionsDialog.iconOp3Id = iconRes;
            optionsDialog.op3Listener = clickListener;
            return this;
        }

        public OptionsDialog create() {
            return optionsDialog;
        }

        public Builder setLabelsEnabled(boolean labelsEnabled) {
            optionsDialog.labelsEnabled = labelsEnabled;
            return this;
        }

        public Builder setAdsDisabled(boolean adsDisabled) {
            optionsDialog.adsDisabled = adsDisabled;
            return this;
        }
    }
}