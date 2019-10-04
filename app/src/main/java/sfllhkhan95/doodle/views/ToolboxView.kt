package sfllhkhan95.doodle.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.utils.listener.OnToolSelectedListener
import java.util.*


/**
 * @author saifkhichi96
 */
class ToolboxView : LinearLayout {

    private val nonSticky = ArrayList<Int>()

    private val primaryToolbox: LinearLayout
    private val secondaryToolbox: LinearLayout

    private var primarySelected = -1
    private var toolSelectedListener: OnToolSelectedListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val params = LayoutParams(context, attrs)
        params.weight = 1f

        primaryToolbox = LinearLayout(context, attrs)
        primaryToolbox.layoutParams = params

        val scrollView = View.inflate(context, R.layout.view_shapes_popup, null)
        scrollView.layoutParams = params

        secondaryToolbox = scrollView.findViewById(R.id.shapes_container)

        val p = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams = p

        this.orientation = LinearLayout.VERTICAL
        this.weightSum = 2f

        addView(scrollView)
        addView(primaryToolbox)
        init()

        try {
            this.setOnToolSelectedListener(context as OnToolSelectedListener)
        } catch (ignored: Exception) {

        }

        secondaryToolbox.visibility = View.GONE
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val params = LayoutParams(context, attrs)
        params.weight = 1f

        primaryToolbox = LinearLayout(context, attrs, defStyleAttr)
        primaryToolbox.layoutParams = params

        val scrollView = View.inflate(context, R.layout.view_shapes_popup, null)
        scrollView.layoutParams = params

        secondaryToolbox = scrollView.findViewById(R.id.shapes_container)

        val p = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams = p

        this.orientation = LinearLayout.VERTICAL
        this.weightSum = 2f

        addView(scrollView)
        addView(primaryToolbox)
        init()

        try {
            this.setOnToolSelectedListener(context as OnToolSelectedListener)
        } catch (ignored: Exception) {

        }

        secondaryToolbox.visibility = View.GONE
    }

    private fun inflateItem(root: LinearLayout, item: MenuItem) {
        val params = LayoutParams(0, LayoutParams.MATCH_PARENT)
        params.weight = 1f

        if (item.isCheckable) {
            nonSticky.add(item.itemId)
        }

        val toolView = View.inflate(context, R.layout.view_tool_icon, null) as LinearLayout
        toolView.layoutParams = params

        val itemView = toolView.findViewById<ImageButton>(R.id.icon)
        itemView?.id = item.itemId
        itemView?.setImageDrawable(item.icon)

        val labelView = toolView.findViewById<TextView>(R.id.label)
        labelView?.text = item.title
        if (root.equals(secondaryToolbox)) labelView.visibility = View.GONE

        itemView.setOnClickListener { v ->
            for (i in 0 until root.childCount) {
                if (v.id == (root.getChildAt(i) as LinearLayout).getChildAt(0).id) {
                    if (item.hasSubMenu()) {
                        if (primarySelected != v.id || secondaryToolbox.visibility == View.GONE) {
                            inflateMenu(secondaryToolbox, item.subMenu)
                            secondaryToolbox.visibility = View.VISIBLE

                            selectTool(primaryToolbox, v.id)

                            if (hasOnToolSelectedListener()) {
                                val id = (secondaryToolbox.getChildAt(0) as LinearLayout).getChildAt(0).id
                                selectTool(secondaryToolbox, id)

                                toolSelectedListener!!.onToolSelected(!nonSticky.contains(id), id)
                            }
                        } else {
                            secondaryToolbox.visibility = View.GONE

                            if (hasOnToolSelectedListener()) {
                                toolSelectedListener!!.onToolSelected(!nonSticky.contains(v.id), v.id)
                            }
                        }
                    } else {
                        if (root == primaryToolbox) {
                            secondaryToolbox.visibility = View.GONE
                            selectTool(primaryToolbox, v.id)
                        } else {
                            selectTool(secondaryToolbox, v.id)
                        }

                        if (hasOnToolSelectedListener()) {
                            toolSelectedListener!!.onToolSelected(!nonSticky.contains(v.id), v.id)
                        }
                    }
                }
            }
        }

        root.addView(toolView)
    }

    private fun hasOnToolSelectedListener(): Boolean {
        return toolSelectedListener != null
    }

    private fun inflateMenu(root: LinearLayout, menu: Menu) {
        root.removeAllViews()

        weightSum = menu.size().toFloat()
        for (i in 0 until menu.size())
            inflateItem(root, menu.getItem(i))
    }

    private fun init() {
        try {
            val popupMenu = PopupMenu(context, null)
            val menu = popupMenu.menu

            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.menu_tools, menu)

            inflateMenu(primaryToolbox, menu)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setToolColor(itemId: Int, color: Int) {
        try {
            (findViewById<View>(itemId) as ImageButton).setColorFilter(color)
        } catch (ignored: Exception) {

        }

    }

    private fun selectTool(root: LinearLayout, id: Int) {
        if (nonSticky.contains(id)) return

        deselectAll(root)
        if (root == primaryToolbox)
            primarySelected = id

        setToolColor(id, ThemeUtils.colorPrimaryDark(context))
    }

    private fun deselectAll(root: LinearLayout) {
        if (root == primaryToolbox)
            primarySelected = -1

        for (i in 0 until root.childCount) {
            setToolColor((root.getChildAt(i) as LinearLayout).getChildAt(0).id, Color.WHITE)
        }
    }

    fun updateFillColorPicker(color: Int) {
        setToolColor(R.id.fillColorPicker, color)
    }

    fun updatePenColorPicker(color: Int) {
        setToolColor(R.id.penColorPicker, color)
    }

    private fun setOnToolSelectedListener(toolSelectedListener: OnToolSelectedListener) {
        this.toolSelectedListener = toolSelectedListener
    }

}
