package sfllhkhan95.doodle.views.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.utils.ThemeUtils

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.3.0
 */
class OptionsDialog private constructor() : BottomSheetDialogFragment() {

    private val options: MutableList<Option> = mutableListOf()
    private var title: String? = null
    private var message: String? = null

    private class Option {
        @DrawableRes
        var icon = -1

        lateinit var title: String
        lateinit var description: String
        lateinit var clickListener: DialogInterface.OnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, ThemeUtils.getDialogTheme())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_options, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Inflate options
        v?.findViewById<LinearLayout>(R.id.optionsList)?.let { optionsList ->
            for (option in options) {
                View.inflate(context, R.layout.view_dialog_option, optionsList)
                val v1 = optionsList.getChildAt(optionsList.childCount - 1)

                v1.findViewById<TextView>(R.id.optionText)?.text = option.title
                v1.findViewById<TextView>(R.id.optionDescription)?.text = option.description
                if (option.icon != -1) {
                    v1.findViewById<ImageView>(R.id.optionIcon)?.setImageResource(option.icon)
                }
                v1.setOnClickListener {
                    option.clickListener.onClick(this@OptionsDialog.dialog, 0)
                }
            }
        }

        // Display ads if they are enabled
        v?.findViewById<AdView>(R.id.adView)?.let {
            AdManager.instance.showBannerAd(it, object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    it.visibility = View.VISIBLE
                }
            })
        }

        title?.let {
            v.findViewById<TextView>(R.id.title).text = it
            v.findViewById<TextView>(R.id.title).visibility = View.VISIBLE
        }

        message?.let {
            v.findViewById<TextView>(R.id.message).text = it
            v.findViewById<TextView>(R.id.message).visibility = View.VISIBLE
        }

        return v
    }

    fun show(manager: FragmentManager) {
        super.show(manager, "options_dialog")
    }

    class Builder {

        private val optionsDialog = OptionsDialog()

        fun addOption(label: String, description: String, @DrawableRes iconRes: Int, clickListener: DialogInterface.OnClickListener): Builder {
            optionsDialog.options.add(Option().apply {
                this.icon = iconRes
                this.title = label
                this.description = description
                this.clickListener = clickListener
            })
            return this
        }

        fun setTitle(title: String): Builder {
            optionsDialog.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            optionsDialog.message = message
            return this
        }

        fun create(): OptionsDialog {
            return optionsDialog
        }

    }

}