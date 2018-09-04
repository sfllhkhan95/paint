package sfllhkhan95.doodle.ads;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import io.fabric.sdk.android.InitializationException;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.billing.BillingManager;

/**
 * AdManager communicates with the Google Mobile SDK to manage the creation of all ads
 * in this application.
 *
 * @author saifkhichi96
 * @version 1.2.2
 * @since 3.4.2
 * created on 04/06/2018 6:36 AM
 */
public class AdManager {

    private static final String ADMOB_APP_ID = "ca-app-pub-6293532072634065~6156179621";
    private static AdManager ourInstance;

    private final BillingManager mBillingManager;

    @Nullable
    private RewardedVideoAd mVideoAd;

    private AdManager(@NonNull Context context) {
        MobileAds.initialize(context, AdManager.ADMOB_APP_ID);
        this.mBillingManager = BillingManager.getInstance(context);
    }

    /**
     * Initializes ad SDK. This method must be called when the application first
     * starts.
     *
     * @param context the application context
     */
    public static void initialize(@NonNull Context context) {
        if (ourInstance == null) {
            ourInstance = new AdManager(context);
        }
    }

    @NonNull
    public static AdManager getInstance() throws InitializationException {
        if (ourInstance == null) {
            throw new InitializationException("AdManager not initialized");
        }

        return ourInstance;
    }

    /**
     * This method makes the one-time purchase which removes all advertisements from
     * the application.
     *
     * @return boolean value indicating the status of ad removal
     * @since 3.4.2
     */
    public boolean removeAds(Activity activity) {
        // If user has not already made this purchase, start the purchase sequence
        if (!hasRemovedAds()) {
            mBillingManager.purchaseProduct(activity, BillingManager.Products.AD_REMOVE);
        }

        return hasRemovedAds();
    }

    /**
     * Returns the amount which user has to pay to remove all ads in app.
     *
     * @return the cost of ad removal
     * @since 3.4.2
     */
    @Nullable
    public String getRemovalPrice() {
        return mBillingManager.getPrice(BillingManager.Products.AD_REMOVE);
    }

    /**
     * This method checks if the in-app ads have been removed or not.
     *
     * @return returns true if user has paid for ad removal or no connection to
     * billing server can be established
     * @since 3.4.2
     */
    public boolean hasRemovedAds() {
        return !mBillingManager.isReady() || mBillingManager.hasPurchased(BillingManager.Products.AD_REMOVE);
    }

    /**
     * Makes an asynchronous request for loading a banner ad into the target view
     * and fires a callback when a response is received.
     *
     * @param target   the {@link AdView} into which the banner ad is to be displayed
     * @param callback this {@link AdListener} listens for response to ad request
     * @since 3.4.2
     */
    public void showBannerAd(@NonNull AdView target, @Nullable AdListener callback) {
        if (!hasRemovedAds()) {
            if (callback != null) {
                target.setAdListener(callback);
            }
            target.loadAd(new AdRequest.Builder().build());
        }
    }

    /**
     * Displays a video ad if one has been loaded.
     *
     * @param target the {@link Activity} where the video should be played
     * @since 3.5.0
     */
    public void showVideoAd(@Nullable final Activity target) {
        if (isVideoAdLoaded() && mVideoAd != null) {
            mVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {

                }

                @Override
                public void onRewardedVideoAdOpened() {

                }

                @Override
                public void onRewardedVideoStarted() {

                }

                @Override
                public void onRewardedVideoAdClosed() {
                    if (target != null) {
                        target.finish();
                    }
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {

                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {

                }

                @Override
                public void onRewardedVideoCompleted() {

                }
            });
            mVideoAd.show();
        }
    }

    /**
     * Makes an asynchronous request for loading a video ad.
     *
     * @param context the {@link Context} which is used to create this ad
     * @since 3.5.0
     */
    public void loadVideoAd(@NonNull Context context) {
        mVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        if (mVideoAd != null) {
            mVideoAd.loadAd(context.getString(R.string.admob_ad_rewarded),
                    new AdRequest.Builder().build());
        }
    }

    /**
     * Returns true if ads are enabled and a video ad has been loaded.
     *
     * @return true if ad loaded, false otherwise
     * @since 3.5.0
     */
    public boolean isVideoAdLoaded() {
        return !hasRemovedAds() && mVideoAd != null && mVideoAd.isLoaded();
    }

}