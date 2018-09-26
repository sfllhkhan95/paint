package sfllhkhan95.doodle.core.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.ads.AdManager
import sfllhkhan95.doodle.core.MainActivity
import sfllhkhan95.doodle.core.views.ConfirmationDialog
import sfllhkhan95.doodle.core.views.OptionsDialog
import sfllhkhan95.doodle.core.views.PaintView

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.4.2
 */
class DialogFactory(private val activity: Activity, private val paintView: PaintView?) {

    private val mAdManager = AdManager.instance

    fun revertConfirmationDialog(context: Context): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_revert))
                .setIcon(R.drawable.ic_action_revert)
                .setTitle("Reset to original?")
                .setMessage("This action will erase all unsaved changes. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener { paintView?.clear() }, true)
                .setNegativeButton(context.getString(android.R.string.cancel), View.OnClickListener {}, true)
                .create()
    }

    fun shareDialog(context: MainActivity): Dialog {
        return OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_tool_shapes)
                .setTitle(context.getString(R.string.menu_action_share))
                .setMessage(context.getString(R.string.description_share))
                .setOption1(context.getString(R.string.label_facebook), R.drawable.ic_facebook, View.OnClickListener { context.shareToFacebook() })
                .setOption2("Messenger", R.drawable.messenger_button_blue_bg_round, View.OnClickListener { context.onShareClicked(true) })
                .setOption3("Default", R.drawable.ic_share, View.OnClickListener { context.onShareClicked(false) })
                .create()
    }

    fun supportDialog(context: Activity): Dialog {
        val mBuilder = OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_action_review)
                .setTitle(context.getString(R.string.settings_icon_support))
                .setMessage(if (mAdManager.hasRemovedAds())
                    "Thank you for supporting Doodle!\n\nDoodle is a free-of-charge, open-source project. Our team is hard at work to bring you the best product. You can support Doodle by reviewing it on Play Store or contributing to its source."
                else
                    "Doodle is a free-of-charge, open-source project. Our team is hard at work to bring you the best product. Minimal ads are the only income source from this app. You can support Doodle by reviewing it on Play Store, contributing to its source, or donating a one-time amount to remove all ads from the app.")
                .setOption1("Review", R.drawable.ic_support_review, View.OnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=sfllhkhan95.doodle"))
                    context.startActivity(browserIntent)
                })
                .setOption2("Contribute", R.drawable.ic_support_github, View.OnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sfllhkhan95/paint"))
                    context.startActivity(browserIntent)
                })

        if (!mAdManager.hasRemovedAds()) {
            val adRemovalPrice = mAdManager.removalPrice
            mBuilder.setOption3(if (adRemovalPrice != null) "Donate $adRemovalPrice" else "Donate",
                    R.drawable.ic_support_donate
                    , View.OnClickListener {
                if (mAdManager.removeAds(context)) {
                    Toast.makeText(context, "Thank you for supporting Doodle! Ads will be removed when you launch the app again.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return mBuilder.setLabelsEnabled(true)
                .create()
    }

    fun saveAsConfirmationDialog(context: Context): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_save))
                .setIcon(R.drawable.ic_action_save_as)
                .setTitle(context.getString(R.string.confirm_save))
                .setMessage(context.getString(R.string.description_save))
                .setPositiveButton(context.getString(R.string.menu_action_save), View.OnClickListener {
                    paintView?.save()
                    activity.finish()
                }, true)
                .setNegativeButton(context.getString(android.R.string.copy), View.OnClickListener {
                    paintView?.saveAs()
                    activity.finish()
                }, true)
                .create()
    }

    fun saveConfirmationDialog(context: Context): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_save))
                .setIcon(R.drawable.ic_action_save)
                .setTitle(context.getString(R.string.confirm_save))
                .setMessage(context.getString(R.string.description_save))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener {
                    paintView?.save()
                    activity.finish()
                }, true)
                .setNegativeButton(context.getString(android.R.string.cancel), View.OnClickListener {}, true)
                .create()
    }

    fun exitConfirmationDialog(context: Context): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.label_exit))
                .setIcon(R.drawable.ic_action_info)
                .setTitle(context.getString(R.string.confirm_exit))
                .setMessage(context.getString(R.string.description_exit))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener { activity.finish() }, true)
                .setIcon(R.drawable.ic_tool_eraser)
                .setNegativeButton(context.getString(R.string.menu_action_save), View.OnClickListener {
                    paintView?.save()
                    activity.finish()
                }, true)
                .create()
    }

}