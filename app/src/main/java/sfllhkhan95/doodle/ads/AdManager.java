package sfllhkhan95.doodle.ads;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import sfllhkhan95.doodle.billing.BillingManager;

/**
 * AdManager communicates with the Google Mobile SDK to manage the creation of all ads
 * in this application.
 *
 * @author saifkhichi96
 * @version 1.1.0
 * @since 3.4.2
 * created on 04/06/2018 6:36 AM
 */
public class AdManager {

    private static final String ADMOB_APP_ID = "ca-app-pub-6293532072634065~6156179621";

    private final Activity mContext;
    private final BillingManager mBillingManager;

    public AdManager(Activity context) {
        this.mContext = context;
        this.mBillingManager = BillingManager.getInstance(context);
    }

    /**
     * This method makes the one-time purchase which removes all advertisements from
     * the application.
     *
     * @return boolean value indicating the status of ad removal
     */
    public boolean removeAds() {
        // If user has not already made this purchase, start the purchase sequence
        if (!hasRemovedAds()) {
            mBillingManager.purchaseProduct(mContext, BillingManager.Products.AD_REMOVE);
        }

        return hasRemovedAds();
    }

    /**
     * This method checks if the in-app ads have been removed or not.
     *
     * @return returns true if user has paid for ad removal or no connection to
     * billing server can be established
     */
    public boolean hasRemovedAds() {
        return !mBillingManager.isReady() || mBillingManager.hasPurchased(BillingManager.Products.AD_REMOVE);
    }

    @Nullable
    public String getRemovalPrice() {
        return mBillingManager.getPrice(BillingManager.Products.AD_REMOVE);
    }

    /**
     * Makes an asynchronous request for loading a banner ad into the target view
     * and fires a callback when a response is received.
     *
     * @param target   the {@link AdView} into which the banner ad is to be displayed
     * @param callback this {@link AdListener} listens for response to ad request
     * @since 3.4.2
     */
    public static void loadBanner(AdView target, AdListener callback) {
        MobileAds.initialize(target.getContext(), AdManager.ADMOB_APP_ID);

        target.setAdListener(callback);
        target.loadAd(new AdRequest.Builder().build());
    }
}