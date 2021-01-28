package sfllhkhan95.doodle.bo.factory

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.views.dialog.OptionsDialog

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.4.2
 */
object DialogFactory {

    private val mAdManager = AdManager.instance

    fun confirmDeleteDialog(context: Context, yes: OnSuccessListener<Void>): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.resources.getString(R.string.label_delete),
                context.resources.getString(R.string.confirm_delete_body),
                R.drawable.ic_action_delete) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun confirmExitAppDialog(context: Context, yes: OnSuccessListener<Void>): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.getString(R.string.label_exit),
                context.getString(R.string.desc_prompt_exit),
                android.R.drawable.ic_menu_close_clear_cancel) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun confirmExitDialog(context: Context, yes: OnSuccessListener<Void>, no: OnSuccessListener<Void>): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.resources.getString(R.string.label_close),
                context.resources.getString(R.string.desc_prompt_close),
                R.drawable.ic_tool_eraser) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .addOption(context.resources.getString(R.string.label_save_and_close),
                context.resources.getString(R.string.desc_prompt_save_and_close),
                R.drawable.ic_action_save) { dialog, _ ->
                no.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun confirmRevertDialog(context: Context, yes: OnSuccessListener<Void>): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.resources.getString(R.string.menu_action_revert),
                context.resources.getString(R.string.desc_prompt_revert),
                R.drawable.ic_action_revert) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun confirmSaveAsDialog(
        context: Context,
        yes: OnSuccessListener<Void>,
        no: OnSuccessListener<Void>,
    ): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.resources.getString(R.string.menu_action_save),
                context.getString(R.string.desc_prompt_save),
                R.drawable.ic_action_save) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .addOption(context.resources.getString(R.string.menu_action_save_as),
                context.getString(R.string.desc_prompt_save_as),
                R.drawable.ic_action_save_as) { dialog, _ ->
                no.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun confirmSaveDialog(context: Context, yes: OnSuccessListener<Void>): OptionsDialog {
        return OptionsDialog.Builder()
            .addOption(context.resources.getString(R.string.menu_action_save),
                context.getString(R.string.desc_prompt_save),
                R.drawable.ic_action_save) { dialog, _ ->
                yes.onSuccess(null)
                dialog.dismiss()
            }
            .create()
    }

    fun supportDialog(context: AppCompatActivity): OptionsDialog {
        val mBuilder = OptionsDialog.Builder()
            .setTitle(context.getString(R.string.settings_item_support))
            .setMessage(if (mAdManager.hasRemovedAds())
                context.getString(R.string.desc_prompt_supported)
            else
                context.getString(R.string.desc_prompt_support))
            .addOption(context.getString(R.string.label_support_review),
                context.getString(R.string.desc_support_review),
                R.drawable.ic_support_review) { _, _ ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_doodle)))
                context.startActivity(browserIntent)
            }
            .addOption(context.getString(R.string.label_support_contribute),
                context.getString(R.string.desc_support_contribute),
                R.drawable.ic_support_github) { _, _ ->
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_doodle_source)))
                context.startActivity(browserIntent)
            }

        if (!mAdManager.hasRemovedAds()) {
            val adRemovalPrice = mAdManager.removalPrice
            mBuilder.addOption(
                context.getString(R.string.label_support_donate) + if (adRemovalPrice != null) " $adRemovalPrice" else "",
                context.getString(R.string.desc_support_donate),
                R.drawable.ic_support_donate) { _, _ ->
                if (mAdManager.removeAds(context)) {
                    Toast.makeText(context, context.getString(R.string.desc_donated), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return mBuilder.create()
    }

}