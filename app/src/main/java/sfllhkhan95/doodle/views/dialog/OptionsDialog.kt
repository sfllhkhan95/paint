package sfllhkhan95.doodle.views.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.utils.ThemeUtils

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.3.0
 */
class OptionsDialog private constructor(context: Context) : BottomSheetDialog(context, ThemeUtils.getDialogTheme()) {

    private val options: MutableList<Option> = mutableListOf()

    private class Option {
        @DrawableRes
        var icon = -1

        lateinit var title: String
        lateinit var description: String
        lateinit var clickListener: DialogInterface.OnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_options)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Inflate options
        findViewById<LinearLayout>(R.id.optionsList)?.let { optionsList ->
            for (option in options) {
                View.inflate(context, R.layout.view_dialog_option, optionsList)
                val v = optionsList.getChildAt(optionsList.childCount - 1)

                v.findViewById<TextView>(R.id.optionText)?.text = option.title
                v.findViewById<TextView>(R.id.optionDescription)?.text = option.description
                if (option.icon != -1) {
                    v.findViewById<ImageView>(R.id.optionIcon)?.setImageResource(option.icon)
                }
                v.setOnClickListener {
                    option.clickListener.onClick(this@OptionsDialog, 0)
                }
            }
        }

        // Display ads if they are enabled
        this.findViewById<AdView>(R.id.adView)?.let {
            AdManager.instance.showBannerAd(it, object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    it.visibility = View.VISIBLE
                }
            })
        }
    }

    class Builder(context: Context) {

        private val optionsDialog: OptionsDialog = OptionsDialog(context)

        fun addOption(label: String, description: String, @DrawableRes iconRes: Int, clickListener: DialogInterface.OnClickListener): Builder {
            optionsDialog.options.add(Option().apply {
                this.icon = iconRes
                this.title = label
                this.description = description
                this.clickListener = clickListener
            })
            return this
        }

        fun create(): OptionsDialog {
            return optionsDialog
        }

    }

}