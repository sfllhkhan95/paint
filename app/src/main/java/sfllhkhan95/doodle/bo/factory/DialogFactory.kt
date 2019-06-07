package sfllhkhan95.doodle.bo.factory

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.views.dialog.ConfirmationDialog
import sfllhkhan95.doodle.views.dialog.OptionsDialog

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.4.2
 */
object DialogFactory {

    private val mAdManager = AdManager.instance

    fun confirmDeleteDialog(context: Context, yes: OnSuccessListener<Void>): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.label_delete))
                .setIcon(R.drawable.ic_action_delete)
                .setTitle(context.resources.getString(R.string.confirm_delete_title))
                .setMessage(context.resources.getString(R.string.confirm_delete_body))
                .setPositiveButton(context.getString(android.R.string.ok),
                        View.OnClickListener {
                            yes.onSuccess(null)
                        }, true)
                .setNegativeButton(context.getString(android.R.string.cancel),
                        View.OnClickListener { }, true)
                .create()
    }

    fun confirmExitDialog(context: Context, yes: OnSuccessListener<Void>, no: OnSuccessListener<Void>): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.label_exit))
                .setIcon(R.drawable.ic_action_info)
                .setTitle(context.getString(R.string.confirm_exit))
                .setMessage(context.getString(R.string.desc_prompt_exit))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener {
                    yes.onSuccess(null)
                }, true)
                .setIcon(R.drawable.ic_tool_eraser)
                .setNegativeButton(context.getString(R.string.menu_action_save), View.OnClickListener {
                    no.onSuccess(null)
                }, true)
                .create()
    }

    fun confirmRevertDialog(context: Context, yes: OnSuccessListener<Void>): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_revert))
                .setIcon(R.drawable.ic_action_revert)
                .setTitle(context.getString(R.string.confirm_revert))
                .setMessage(context.getString(R.string.desc_prompt_revert))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener { yes.onSuccess(null) }, true)
                .setNegativeButton(context.getString(android.R.string.cancel), View.OnClickListener {}, true)
                .create()
    }

    fun confirmSaveAsDialog(context: Context, yes: OnSuccessListener<Void>, no: OnSuccessListener<Void>): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_save))
                .setIcon(R.drawable.ic_action_save_as)
                .setTitle(context.getString(R.string.confirm_save))
                .setMessage(context.getString(R.string.desc_prompt_save))
                .setPositiveButton(context.getString(R.string.menu_action_save), View.OnClickListener {
                    yes.onSuccess(null)
                }, true)
                .setNegativeButton(context.getString(android.R.string.copy), View.OnClickListener {
                    no.onSuccess(null)
                }, true)
                .create()
    }

    fun confirmSaveDialog(context: Context, yes: OnSuccessListener<Void>): Dialog {
        return ConfirmationDialog.Builder(context)
                .setHeadline(context.getString(R.string.menu_action_save))
                .setIcon(R.drawable.ic_action_save)
                .setTitle(context.getString(R.string.confirm_save))
                .setMessage(context.getString(R.string.desc_prompt_save))
                .setPositiveButton(context.getString(android.R.string.yes), View.OnClickListener {
                    yes.onSuccess(null)
                }, true)
                .setNegativeButton(context.getString(android.R.string.cancel), View.OnClickListener {}, true)
                .create()
    }

    fun shareDialog(context: Context, callback1: View.OnClickListener, callback2: View.OnClickListener, callback3: View.OnClickListener): Dialog {
        return OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_tool_shapes)
                .setTitle(context.getString(R.string.menu_action_share))
                .setMessage(context.getString(R.string.desc_prompt_share))
                .setOption1(context.getString(R.string.label_share_whatsapp), R.drawable.ic_whatsapp, callback1)
                .setOption2(context.getString(R.string.label_share_messenger), R.drawable.messenger_button_blue_bg_round, callback2)
                .setOption3(context.getString(R.string.label_share_default), R.drawable.ic_share, callback3)
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
            mBuilder.setOption3(
                    context.getString(R.string.label_support_donate) + if (adRemovalPrice != null) " $adRemovalPrice" else "",
                    R.drawable.ic_support_donate,
                    View.OnClickListener {
                        if (mAdManager.removeAds(context)) {
                            Toast.makeText(context, context.getString(R.string.desc_donated), Toast.LENGTH_SHORT).show()
                        }
                    }
            )
        }

        return mBuilder.setLabelsEnabled(true)
                .create()
    }

}