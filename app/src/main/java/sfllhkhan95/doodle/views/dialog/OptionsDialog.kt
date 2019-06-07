package sfllhkhan95.doodle.views.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.3.0
 */
class OptionsDialog private constructor(context: Context) : Dialog(context) {

    private var dialogIcon: ImageView? = null
    private var titleView: TextView? = null
    private var messageView: TextView? = null

    private var option1: String? = null
    private var option1Icon: ImageView? = null
    private var option1Label: TextView? = null
    private var op1Listener: View.OnClickListener? = null

    private var option2: String? = null
    private var option2Icon: ImageView? = null
    private var option2Label: TextView? = null
    private var op2Listener: View.OnClickListener? = null

    private var option3: String? = null
    private var option3Icon: ImageView? = null
    private var option3Label: TextView? = null
    private var op3Listener: View.OnClickListener? = null

    private var title: String? = null
    private var message: String? = null

    @DrawableRes
    private var iconId = -1

    @DrawableRes
    private var iconOp1Id = -1

    @DrawableRes
    private var iconOp2Id = -1

    @DrawableRes
    private var iconOp3Id = -1

    private var labelsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_options)

        // Get UI elements
        dialogIcon = findViewById(R.id.icon)
        titleView = findViewById(R.id.title)
        messageView = findViewById(R.id.message)

        option1Icon = findViewById(R.id.op1_icon)
        option1Label = findViewById(R.id.op1_label)

        option2Icon = findViewById(R.id.op2_icon)
        option2Label = findViewById(R.id.op2_label)

        option3Icon = findViewById(R.id.op3_icon)
        option3Label = findViewById(R.id.op3_label)

        // Configure UI elements
        if (iconId != -1) {
            dialogIcon?.setImageResource(iconId)
        }
        titleView?.text = title
        messageView?.text = message

        findViewById<View>(R.id.op1).visibility = View.GONE
        findViewById<View>(R.id.op2).visibility = View.GONE
        findViewById<View>(R.id.op3).visibility = View.GONE

        if (iconOp1Id != -1) {
            findViewById<View>(R.id.op1).visibility = View.VISIBLE
            option1Icon?.setImageResource(iconOp1Id)
            option1Label?.text = option1

            option1Icon?.setOnClickListener(op1Listener)
            option1Label?.setOnClickListener(op1Listener)
        }

        if (iconOp2Id != -1) {
            findViewById<View>(R.id.op2).visibility = View.VISIBLE
            option2Icon?.setImageResource(iconOp2Id)
            option2Label?.text = option2

            option2Icon?.setOnClickListener(op2Listener)
            option2Label?.setOnClickListener(op2Listener)
        }

        if (iconOp3Id != -1) {
            findViewById<View>(R.id.op3).visibility = View.VISIBLE
            option3Icon?.setImageResource(iconOp3Id)
            option3Label?.text = option3

            option3Icon?.setOnClickListener(op3Listener)
            option3Label?.setOnClickListener(op3Listener)
        }

        if (labelsEnabled) {
            showLabels()
        } else {
            hideLabels()
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

    private fun showLabels() {
        option1Label?.visibility = View.VISIBLE
        option2Label?.visibility = View.VISIBLE
        option3Label?.visibility = View.VISIBLE
    }

    private fun hideLabels() {
        option1Label?.visibility = View.GONE
        option2Label?.visibility = View.GONE
        option3Label?.visibility = View.GONE
    }

    class Builder(context: Context) {

        private val optionsDialog: OptionsDialog = OptionsDialog(context)

        fun setIcon(iconId: Int): Builder {
            optionsDialog.iconId = iconId
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

        fun setOption1(label: String, @DrawableRes iconRes: Int, clickListener: View.OnClickListener): Builder {
            optionsDialog.option1 = label
            optionsDialog.iconOp1Id = iconRes
            optionsDialog.op1Listener = clickListener
            return this
        }

        fun setOption2(label: String, @DrawableRes iconRes: Int, clickListener: View.OnClickListener): Builder {
            optionsDialog.option2 = label
            optionsDialog.iconOp2Id = iconRes
            optionsDialog.op2Listener = clickListener
            return this
        }

        fun setOption3(label: String, @DrawableRes iconRes: Int, clickListener: View.OnClickListener): Builder {
            optionsDialog.option3 = label
            optionsDialog.iconOp3Id = iconRes
            optionsDialog.op3Listener = clickListener
            return this
        }

        fun create(): OptionsDialog {
            return optionsDialog
        }

        fun setLabelsEnabled(labelsEnabled: Boolean): Builder {
            optionsDialog.labelsEnabled = labelsEnabled
            return this
        }

    }
}