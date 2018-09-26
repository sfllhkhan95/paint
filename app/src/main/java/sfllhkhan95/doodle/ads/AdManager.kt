package sfllhkhan95.doodle.ads

import android.app.Activity
import android.content.Context

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

import io.fabric.sdk.android.InitializationException
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.billing.BillingManager

/**
 * AdManager communicates with the Google Mobile SDK to manage the creation of all ads
 * in this application.
 *
 * @author saifkhichi96
 * @version 1.2.2
 * @since 3.4.2
 * created on 04/06/2018 6:36 AM
 */
class AdManager private constructor(context: Context) {

    private val mBillingManager: BillingManager

    private var mVideoAd: RewardedVideoAd? = null

    /**
     * Returns the amount which user has to pay to remove all ads in app.
     *
     * @return the cost of ad removal
     * @since 3.4.2
     */
    val removalPrice: String?
        get() = mBillingManager.getPrice(BillingManager.Products.AD_REMOVE)

    /**
     * Returns true if ads are enabled and a video ad has been loaded.
     *
     * @return true if ad loaded, false otherwise
     * @since 3.5.0
     */
    val isVideoAdLoaded: Boolean
        get() = !hasRemovedAds() && mVideoAd != null && mVideoAd!!.isLoaded

    init {
        MobileAds.initialize(context, AdManager.ADMOB_APP_ID)
        this.mBillingManager = BillingManager.getInstance(context)
    }

    /**
     * This method makes the one-time purchase which removes all advertisements from
     * the application.
     *
     * @return boolean value indicating the status of ad removal
     * @since 3.4.2
     */
    fun removeAds(activity: Activity): Boolean {
        // If user has not already made this purchase, start the purchase sequence
        if (!hasRemovedAds()) {
            mBillingManager.purchaseProduct(activity, BillingManager.Products.AD_REMOVE)
        }

        return hasRemovedAds()
    }

    /**
     * This method checks if the in-app ads have been removed or not.
     *
     * @return returns true if user has paid for ad removal or no connection to
     * billing server can be established
     * @since 3.4.2
     */
    fun hasRemovedAds(): Boolean {
        return !mBillingManager.isReady || mBillingManager.hasPurchased(BillingManager.Products.AD_REMOVE)
    }

    /**
     * Makes an asynchronous request for loading a banner ad into the target view
     * and fires a callback when a response is received.
     *
     * @param target   the [AdView] into which the banner ad is to be displayed
     * @param callback this [AdListener] listens for response to ad request
     * @since 3.4.2
     */
    fun showBannerAd(target: AdView, callback: AdListener?) {
        if (!hasRemovedAds()) {
            if (callback != null) {
                target.adListener = callback
            }
            target.loadAd(AdRequest.Builder().build())
        }
    }

    /**
     * Displays a video ad if one has been loaded.
     *
     * @param target the [Activity] where the video should be played
     * @since 3.5.0
     */
    fun showVideoAd(target: Activity?) {
        if (isVideoAdLoaded && mVideoAd != null) {
            mVideoAd!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
                override fun onRewardedVideoAdLoaded() {

                }

                override fun onRewardedVideoAdOpened() {

                }

                override fun onRewardedVideoStarted() {

                }

                override fun onRewardedVideoAdClosed() {
                    target?.finish()
                }

                override fun onRewarded(rewardItem: RewardItem) {

                }

                override fun onRewardedVideoAdLeftApplication() {

                }

                override fun onRewardedVideoAdFailedToLoad(i: Int) {

                }

                override fun onRewardedVideoCompleted() {

                }
            }
            mVideoAd!!.show()
        }
    }

    /**
     * Makes an asynchronous request for loading a video ad.
     *
     * @param context the [Context] which is used to create this ad
     * @since 3.5.0
     */
    fun loadVideoAd(context: Context) {
        mVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        if (mVideoAd != null) {
            mVideoAd!!.loadAd(context.getString(R.string.admob_ad_rewarded),
                    AdRequest.Builder().build())
        }
    }

    companion object {

        private const val ADMOB_APP_ID = "ca-app-pub-6293532072634065~6156179621"
        private var ourInstance: AdManager? = null

        /**
         * Initializes ad SDK. This method must be called when the application first
         * starts.
         *
         * @param context the application context
         */
        fun initialize(context: Context) {
            if (ourInstance == null) {
                ourInstance = AdManager(context)
            }
        }

        val instance: AdManager
            @Throws(InitializationException::class)
            get() {
                if (ourInstance == null) {
                    throw InitializationException("AdManager not initialized")
                }

                return ourInstance as AdManager
            }
    }

}