package sfllhkhan95.doodle.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.core.views.ConfirmationDialog;
import sfllhkhan95.doodle.core.views.OptionsDialog;
import sfllhkhan95.doodle.core.views.PaintView;

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.4.2
 */
public class DialogFactory {

    private final AdManager mAdManager = AdManager.getInstance();

    private final PaintView paintView;
    private final Activity activity;

    public DialogFactory(Activity activity, PaintView paintView) {
        this.activity = activity;
        this.paintView = paintView;
    }

    public Dialog revertConfirmationDialog(Context context) {
        return new ConfirmationDialog.Builder(context)
                .setHeadline("Revert")
                .setIcon(R.drawable.ic_action_revert)
                .setTitle("Reset to original?")
                .setMessage("This action will erase all unsaved changes. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton("Reset", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintView.clear();
                    }
                }, true)
                .setNegativeButton("Cancel", null, true)
                .create();
    }

    public Dialog shareDialog(final MainActivity context) {
        return new OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_tool_shapes)
                .setTitle("Share")
                .setMessage("Post to Facebook, send Doodle in a Messenger conversation, or share through other applications")
                .setOption1("Facebook", R.drawable.ic_facebook, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.shareToFacebook();
                    }
                })
                .setOption2("Messenger", R.drawable.messenger_button_blue_bg_round, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.onShareClicked(true);
                    }
                })
                .setOption3("Default", R.drawable.ic_share, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.onShareClicked(false);
                    }
                })
                .create();
    }

    public Dialog supportDialog(final Activity context) {
        OptionsDialog.Builder mBuilder = new OptionsDialog.Builder(context)
                .setIcon(R.drawable.ic_action_review)
                .setTitle("Support Us")
                .setMessage(mAdManager.hasRemovedAds()
                        ? "Thank you for supporting Doodle!\n\nDoodle is a free-of-charge, open-source project. Our team is hard at work to bring you the best product. You can support Doodle by reviewing it on Play Store or contributing to its source."
                        : "Doodle is a free-of-charge, open-source project. Our team is hard at work to bring you the best product. Minimal ads are the only income source from this app. You can support Doodle by reviewing it on Play Store, contributing to its source, or donating a one-time amount to remove all ads from the app.")
                .setOption1("Review", R.drawable.ic_support_review, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=sfllhkhan95.doodle"));
                        context.startActivity(browserIntent);
                    }
                })
                .setOption2("Contribute", R.drawable.ic_support_github, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sfllhkhan95/paint"));
                        context.startActivity(browserIntent);
                    }
                });

        if (!mAdManager.hasRemovedAds()) {
            final String adRemovalPrice = mAdManager.getRemovalPrice();
            mBuilder.setOption3(adRemovalPrice != null ? "Donate " + adRemovalPrice : "Donate",
                    R.drawable.ic_support_donate,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mAdManager.removeAds(context)) {
                                Toast.makeText(context, "Thank you for supporting Doodle! Ads will be removed when you launch the app again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return mBuilder.setLabelsEnabled(true)
                .create();
    }

    public Dialog saveAsConfirmationDialog(Context context) {
        return new ConfirmationDialog.Builder(context)
                .setHeadline("Save")
                .setIcon(R.drawable.ic_action_save_as)
                .setTitle("Save the updated Doodle?")
                .setMessage("Your existing project will be updated in Gallery, and you can continue editing it later.")
                .setPositiveButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .setNegativeButton("Save Copy", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.saveAs();
                        activity.finish();
                    }
                }, true)
                .create();
    }

    public Dialog saveConfirmationDialog(Context context) {
        return new ConfirmationDialog.Builder(context)
                .setHeadline("Save")
                .setIcon(R.drawable.ic_action_save)
                .setTitle("Save the current Doodle?")
                .setMessage("Your project will be saved to Gallery, and you can continue editing it later.")
                .setPositiveButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .setNegativeButton("Cancel", null, true)
                .create();
    }

    public Dialog exitConfirmationDialog(Context context) {
        return new ConfirmationDialog.Builder(context)
                .setHeadline("Exit")
                .setIcon(R.drawable.ic_action_info)
                .setTitle("Exit without saving?")
                .setMessage("All unsaved changes would be discarded. Do you really wish to proceed?")
                .setPositiveButton("Discard", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                }, true)
                .setIcon(R.drawable.ic_tool_eraser)
                .setNegativeButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .create();
    }

}