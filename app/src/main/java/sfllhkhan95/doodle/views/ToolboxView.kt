package sfllhkhan95.doodle.views

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
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

    private var toolSelectedListener: OnToolSelectedListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val params = LayoutParams(context, attrs)

        primaryToolbox = LinearLayout(context, attrs)
        primaryToolbox.layoutParams = params
        primaryToolbox.weightSum = 5f

        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL

        addView(primaryToolbox)
        init()

        try {
            this.setOnToolSelectedListener(context as OnToolSelectedListener)
        } catch (ignored: Exception) {

        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val params = LayoutParams(context, attrs)

        primaryToolbox = LinearLayout(context, attrs, defStyleAttr)
        primaryToolbox.layoutParams = params
        primaryToolbox.weightSum = 5f

        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL

        addView(primaryToolbox)
        init()

        try {
            this.setOnToolSelectedListener(context as OnToolSelectedListener)
        } catch (ignored: Exception) {

        }
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

        itemView.setOnClickListener { v ->
            for (i in 0 until root.childCount) {
                if (v.id == (root.getChildAt(i) as LinearLayout).getChildAt(0).id) {
                    selectTool(primaryToolbox, v.id)

                    toolSelectedListener?.onToolSelected(!nonSticky.contains(v.id), v.id)
                }
            }
        }

        if (item.itemId == R.id.space) {
            toolView.visibility = View.INVISIBLE
        }

        root.addView(toolView)
    }

    private fun inflateMenu(root: LinearLayout, menu: Menu) {
        root.removeAllViews()

        for (i in 0 until menu.size()) {
            inflateItem(root, menu.getItem(i))
        }
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
        // setToolColor(id, ThemeUtils.colorAccent(context))
    }

    private fun deselectAll(root: LinearLayout) {
        for (i in 0 until root.childCount) {
            setToolColor((root.getChildAt(i) as LinearLayout).getChildAt(0).id, ThemeUtils.colorTextPrimary(context))
        }
    }

    private fun setOnToolSelectedListener(toolSelectedListener: OnToolSelectedListener) {
        this.toolSelectedListener = toolSelectedListener
    }

}
