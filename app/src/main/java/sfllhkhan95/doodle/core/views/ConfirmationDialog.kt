package sfllhkhan95.doodle.core.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.ads.AdManager

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.4.2
 */
class ConfirmationDialog(context: Context) : Dialog(context) {

    private var title: String? = null
    private var headline: String? = null
    private var message: String? = null

    @DrawableRes
    private var icon = -1

    private var positiveButtonListener: View.OnClickListener? = null
    private var positiveButtonLabel = ""
    private var dismissAfterPositive = false

    private var negativeButtonListener: View.OnClickListener? = null
    private var negativeButtonLabel = ""
    private var dismissAfterNegative = false

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        if (window != null) {
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContentView(R.layout.dialog_confirmation)

        val titleView = findViewById<TextView>(R.id.title)
        titleView.text = title

        val headlineView = findViewById<TextView>(R.id.headline)
        headlineView.text = headline

        val descriptionView = findViewById<TextView>(R.id.message)
        descriptionView.text = message

        if (icon != -1) {
            val iconView = findViewById<ImageView>(R.id.icon)
            iconView.setImageResource(icon)
        }

        if (!positiveButtonLabel.isEmpty()) {
            val positiveButton = findViewById<Button>(R.id.positiveButton)
            positiveButton.text = positiveButtonLabel
            positiveButton.setOnClickListener {
                if (positiveButtonListener != null) {
                    positiveButtonListener!!.onClick(positiveButton)
                }
                if (dismissAfterPositive) {
                    dismiss()
                }
            }
            positiveButton.visibility = View.VISIBLE
        }

        if (!negativeButtonLabel.isEmpty()) {
            val negativeButton = findViewById<Button>(R.id.negativeButton)
            negativeButton.text = negativeButtonLabel
            negativeButton.setOnClickListener {
                if (negativeButtonListener != null) {
                    negativeButtonListener!!.onClick(negativeButton)
                }
                if (dismissAfterNegative) {
                    dismiss()
                }
            }
            negativeButton.visibility = View.VISIBLE
        }

        // Display ads if they are enabled
        val mAdView = this.findViewById<AdView>(R.id.adView)
        AdManager.instance.showBannerAd(mAdView, object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }
        })
    }

    class Builder(context: Context) {

        private val dialog: ConfirmationDialog = ConfirmationDialog(context)

        fun setTitle(title: String): Builder {
            dialog.title = title
            return this
        }

        fun setHeadline(headline: String): Builder {
            dialog.headline = headline
            return this
        }

        fun setMessage(message: String): Builder {
            dialog.message = message
            return this
        }

        fun setIcon(icon: Int): Builder {
            dialog.icon = icon
            return this
        }

        fun setPositiveButton(label: String, listener: View.OnClickListener, dismiss: Boolean): Builder {
            dialog.positiveButtonLabel = label
            dialog.positiveButtonListener = listener
            dialog.dismissAfterPositive = dismiss
            return this
        }

        fun setNegativeButton(label: String, listener: View.OnClickListener, dismiss: Boolean): Builder {
            dialog.negativeButtonLabel = label
            dialog.negativeButtonListener = listener
            dialog.dismissAfterNegative = dismiss
            return this
        }

        fun create(): Dialog {
            return dialog
        }

    }

}