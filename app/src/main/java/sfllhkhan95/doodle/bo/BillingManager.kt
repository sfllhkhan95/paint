package sfllhkhan95.doodle.bo

import android.content.Context
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

/**
 * @author saifkhichi96
 * @version 2.0.0
 * @since 3.4.0 2018-06-04 00:29
 */
class BillingManager private constructor(context: Context) :
        PurchasesUpdatedListener, BillingClientStateListener {

    /**
     * List of all purchasable products in this application as (Product ID, Details) pairs.
     */
    private val allProducts = HashMap<String, SkuDetails>()

    /**
     * List of all purchased products purchased by the current user as
     * (Product ID, Order ID) pairs.
     */
    private val purchasedProducts = HashMap<String, String>()

    /**
     * This object is used to communicate with the billing server.
     */
    private val mBillingClient: BillingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()

    /**
     * Checks if the client is currently connected to the service, so that requests to other methods
     * will succeed.
     *
     * @return Returns true if the client is currently connected to the service, false otherwise.
     */
    val isReady: Boolean
        get() = mBillingClient.isReady

    init {

        mBillingClient.startConnection(this)
    }

    private fun syncInventory() {
        // Create a list of all products
        val productIds = ArrayList<String>()
        productIds.add(Products.AD_REMOVE)

        // Fetch details of all products from the billing server
        mBillingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder()
                        .setSkusList(productIds)
                        .setType(BillingClient.SkuType.INAPP)
                        .build()

        ) { response, products ->
            if (response.responseCode == BillingClient.BillingResponseCode.OK && products != null) {
                for (product in products) {
                    allProducts[product.sku] = product
                }
            }
        }

        // Update user's purchases
        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            onPurchasesUpdated(billingResult, purchases)
        }
    }

    /**
     * Returns the details of the specified purchasable product.
     *
     * @param productId unique id of the product
     * @return [SkuDetails] object for the specified product id, or null if no
     * such product exists
     */
    private fun getDetails(productId: String): SkuDetails? {
        return allProducts[productId]
    }

    /**
     * Returns the price of the specified purchasable product.
     *
     * @param productId unique id of the product
     * @return price for the specified product id, or null if no such product exists
     */
    fun getPrice(productId: String): String? {
        return if (allProducts.containsKey(productId)) allProducts[productId]?.price else null
    }

    /**
     * Makes a purchase request for the specified one-time in-app product.
     *
     * @param context   the activity from which purchase request is launched
     * @param productId unique id of the product to purchase
     * @return [BillingClient.BillingResponseCode] indicating status of purcahse request
     * of the purchase request
     */
    fun purchaseProduct(context: AppCompatActivity, productId: String): Int {
        // If the specified product is not in inventory, return proper error code
        val details = getDetails(productId)
        if (!allProducts.containsKey(productId) || details == null) {
            return BillingClient.BillingResponseCode.ITEM_UNAVAILABLE
        }

        // Build a purchase request
        val params = BillingFlowParams.newBuilder()
                .setSkuDetails(details)
                .build()

        // Start the purchase sequence
        val responseCode = mBillingClient.launchBillingFlow(context, params).responseCode

        // If Play Store says user already owns this product, update local purchases' info
        if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED &&
                !purchasedProducts.containsKey(productId)) {
            purchasedProducts[productId] = ""
        }

        // Return purchase request status
        return responseCode
    }

    /**
     * This method is called when an update about a purchased product is received. It logs the
     * update in local inventory.
     *
     * @param purchase the product which was purchased
     */
    private fun onItemPurchased(purchase: Purchase) {
        val orderId = purchase.orderId
        val productId = purchase.skus.firstOrNull()

        if (productId != null && (!purchasedProducts.containsKey(productId) || purchasedProducts[productId] == null)) {
            purchasedProducts[productId] = orderId
        }
    }

    /**
     * Checks if the user has purchased the specified product or not.
     *
     * @param productId Unique if of the product to check
     * @return Returns true if the product is owned by the user, false otherwise
     */
    fun hasPurchased(productId: String): Boolean {
        return purchasedProducts.containsKey(productId)
    }

    /**
     * This callback is fired when purchases are updated. Both purchases initiated by
     * your app and the ones initiated by Play Store will be reported here.
     *
     * @param result    Result of the billing request
     * @param purchases List of updated purchases if present
     */
    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                onItemPurchased(purchase)
            }
        }
    }

    /**
     * This callback is fired when configuration of the billing server is finished. If
     * a connection was successfully established, local inventory and purchase details
     * are updated.
     *
     * @param result this code indicates whether the configuration was successful
     * or not
     */
    override fun onBillingSetupFinished(result: BillingResult) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            syncInventory()
        }
    }

    /**
     * This callback is fired if connection to billing server is lost. It tries to
     * reconnect to the server after 5 seconds.
     */
    override fun onBillingServiceDisconnected() {
        Handler().postDelayed({ mBillingClient.startConnection(this@BillingManager) }, 5000L)
    }

    object Products {
        const val AD_REMOVE = "doodle.once.adremove"
    }

    companion object {

        /**
         * Shared instance of the BillingManager.
         */
        private var ourInstance: BillingManager? = null

        fun getInstance(context: Context): BillingManager {
            if (ourInstance == null) {
                ourInstance = BillingManager(context)
            }
            return ourInstance as BillingManager
        }
    }

}