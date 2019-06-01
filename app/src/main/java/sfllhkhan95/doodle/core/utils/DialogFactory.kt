package sfllhkhan95.doodle.core.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
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
class DialogFactory(private val activity: AppCompatActivity, private val paintView: PaintView?) {

    private val mAdManager = AdManager.instance

    fun revertConfirmationDialog(context: Context): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_revert))
                .setIcon(R.drawable.ic_action_revert)
                .setTitle(context.getString(R.string.confirm_revert))
                .setMessage(context.getString(R.string.desc_prompt_revert))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener { paintView?.clear() }, true)
                .setNegativeButton(context.getString(android.R.string.cancel), View.OnClickListener {}, true)
                .create()
    }

    fun shareDialog(context: MainActivity): Dialog {
        return OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_tool_shapes)
                .setTitle(context.getString(R.string.menu_action_share))
                .setMessage(context.getString(R.string.desc_prompt_share))
                .setOption1(context.getString(R.string.label_share_whatsapp), R.drawable.ic_whatsapp, View.OnClickListener { context.onShareClicked(MainActivity.ShareMethod.WHATSAPP) })
                .setOption2(context.getString(R.string.label_share_messenger), R.drawable.messenger_button_blue_bg_round, View.OnClickListener { context.onShareClicked(MainActivity.ShareMethod.MESSENGER) })
                .setOption3(context.getString(R.string.label_share_default), R.drawable.ic_share, View.OnClickListener { context.onShareClicked(MainActivity.ShareMethod.DEFAULT) })
                .create()
    }

    fun supportDialog(context: AppCompatActivity): Dialog {
        val mBuilder = OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_action_review)
                .setTitle(context.getString(R.string.settings_icon_support))
                .setMessage(if (mAdManager.hasRemovedAds())
                    context.getString(R.string.desc_prompt_supported)
                else
                    context.getString(R.string.desc_prompt_support))
                .setOption1(context.getString(R.string.label_support_review), R.drawable.ic_support_review, View.OnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_doodle)))
                    context.startActivity(browserIntent)
                })
                .setOption2(context.getString(R.string.label_support_contribute), R.drawable.ic_support_github, View.OnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_doodle_source)))
                    context.startActivity(browserIntent)
                })

        if (!mAdManager.hasRemovedAds()) {
            val adRemovalPrice = mAdManager.removalPrice
            mBuilder.setOption3(if (adRemovalPrice != null) context.getString(R.string.label_support_donate) + " $adRemovalPrice" else context.getString(R.string.label_support_donate),
                    R.drawable.ic_support_donate
                    , View.OnClickListener {
                if (mAdManager.removeAds(context)) {
                    Toast.makeText(context, context.getString(R.string.desc_donated), Toast.LENGTH_SHORT).show()
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
                .setMessage(context.getString(R.string.desc_prompt_save))
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
                .setMessage(context.getString(R.string.desc_prompt_save))
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
                .setMessage(context.getString(R.string.desc_prompt_exit))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener { activity.finish() }, true)
                .setIcon(R.drawable.ic_tool_eraser)
                .setNegativeButton(context.getString(R.string.menu_action_save), View.OnClickListener {
                    paintView?.save()
                    activity.finish()
                }, true)
                .create()
    }

}