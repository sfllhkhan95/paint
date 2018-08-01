package sfllhkhan95.doodle.core.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.4.2
 */
public class ConfirmationDialog extends Dialog {

    private String title;
    private String headline;
    private String message;

    @DrawableRes
    private int icon = -1;

    private View.OnClickListener positiveButtonListener;
    private String positiveButtonLabel;
    private boolean dismissAfterPositive = false;

    private View.OnClickListener negativeButtonListener;
    private String negativeButtonLabel;
    private boolean dismissAfterNegative = false;

    private boolean adsDisabled = true;

    public ConfirmationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(R.layout.dialog_confirmation);

        TextView titleView = findViewById(R.id.title);
        titleView.setText(title);

        TextView headlineView = findViewById(R.id.headline);
        headlineView.setText(headline);

        TextView descriptionView = findViewById(R.id.message);
        descriptionView.setText(message);

        if (icon != -1) {
            ImageView iconView = findViewById(R.id.icon);
            iconView.setImageResource(icon);
        }

        if (!positiveButtonLabel.isEmpty()) {
            final Button positiveButton = findViewById(R.id.positiveButton);
            positiveButton.setText(positiveButtonLabel);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (positiveButtonListener != null) {
                        positiveButtonListener.onClick(positiveButton);
                    }
                    if (dismissAfterPositive) {
                        dismiss();
                    }
                }
            });
            positiveButton.setVisibility(View.VISIBLE);
        }

        if (!negativeButtonLabel.isEmpty()) {
            final Button negativeButton = findViewById(R.id.negativeButton);
            negativeButton.setText(negativeButtonLabel);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (negativeButtonListener != null) {
                        negativeButtonListener.onClick(negativeButton);
                    }
                    if (dismissAfterNegative) {
                        dismiss();
                    }
                }
            });
            negativeButton.setVisibility(View.VISIBLE);
        }

        // Display ads if they are enabled
        if (!adsDisabled) {
            final AdView mAdView = this.findViewById(R.id.adView);
            AdManager.loadBanner(mAdView, new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public static class Builder {

        private final ConfirmationDialog dialog;

        public Builder(Context context) {
            this.dialog = new ConfirmationDialog(context);
        }

        public Builder setTitle(String title) {
            dialog.title = title;
            return this;
        }

        public Builder setHeadline(String headline) {
            dialog.headline = headline;
            return this;
        }

        public Builder setMessage(String message) {
            dialog.message = message;
            return this;
        }

        public Builder setIcon(int icon) {
            dialog.icon = icon;
            return this;
        }

        public Builder setPositiveButton(String label, View.OnClickListener listener, boolean dismiss) {
            dialog.positiveButtonLabel = label;
            dialog.positiveButtonListener = listener;
            dialog.dismissAfterPositive = dismiss;
            return this;
        }

        public Builder setNegativeButton(String label, View.OnClickListener listener, boolean dismiss) {
            dialog.negativeButtonLabel = label;
            dialog.negativeButtonListener = listener;
            dialog.dismissAfterNegative = dismiss;
            return this;
        }

        public Builder setAdsDisabled(boolean adsDisabled) {
            dialog.adsDisabled = adsDisabled;
            return this;
        }

        public Dialog create() {
            return dialog;
        }

    }

}