package sfllhkhan95.doodle.billing;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saifkhichi96
 * @version 1.0.2
 * @since 3.4.0
 * created on 04/06/2018 12:29 AM
 */
public class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener {

    /**
     * Shared instance of the BillingManager.
     */
    private static BillingManager ourInstance;

    /**
     * List of all purchasable products in this application as (Product ID, Details) pairs.
     */
    private final Map<String, SkuDetails> allProducts = new HashMap<>();

    /**
     * List of all purchased products purchased by the current user as
     * (Product ID, Order ID) pairs.
     */
    private final Map<String, String> purchasedProducts = new HashMap<>();

    /**
     * This object is used to communicate with the billing server.
     */
    private final BillingClient mBillingClient;

    public static BillingManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new BillingManager(context);
        }
        return ourInstance;
    }

    private BillingManager(Context context) {
        mBillingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .build();

        mBillingClient.startConnection(this);
    }

    private void syncInventory() {
        // Create a list of all products
        List<String> productIds = new ArrayList<>();
        productIds.add(Products.AD_REMOVE);

        // Fetch details of all products from the billing server
        mBillingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder()
                        .setSkusList(productIds)
                        .setType(BillingClient.SkuType.INAPP)
                        .build(),

                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> products) {
                        if (responseCode == BillingClient.BillingResponse.OK && products != null) {
                            for (SkuDetails product : products) {
                                allProducts.put(product.getSku(), product);
                            }
                        }
                    }
                });

        // Update list of purchased items from local cache
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        onPurchasesUpdated(purchasesResult.getResponseCode(), purchasesResult.getPurchasesList());

        // Synchronise purchases with remote server
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
                onPurchasesUpdated(responseCode, purchasesList);
            }
        });
    }

    /**
     * Returns the details of the specified purchasable product.
     *
     * @param productId unique id of the product
     * @return {@link SkuDetails} object for the specified product id, or null if no
     * such product exists
     */
    @Nullable
    public SkuDetails getDetails(String productId) {
        return allProducts.get(productId);
    }

    /**
     * Returns the price of the specified purchasable product.
     *
     * @param productId unique id of the product
     * @return price for the specified product id, or null if no such product exists
     */
    @Nullable
    public String getPrice(String productId) {
        return allProducts.containsKey(productId) ? allProducts.get(productId).getPrice() : null;
    }

    /**
     * Makes a purchase request for the specified one-time in-app product.
     *
     * @param context   the activity from which purchase request is launched
     * @param productId unique id of the product to purchase
     * @return {@link com.android.billingclient.api.BillingClient.BillingResponse} indicating status
     * of the purchase request
     */
    public int purchaseProduct(Activity context, String productId) {
        // If the specified product is not in inventory, return proper error code
        if (!allProducts.containsKey(productId)) {
            return BillingClient.BillingResponse.ITEM_UNAVAILABLE;
        }

        // Build a purchase request
        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setSku(productId)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();

        // Start the purchase sequence
        int responseCode = mBillingClient.launchBillingFlow(context, params);

        // If Play Store says user already owns this product, update local purchases' info
        if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED
                && !purchasedProducts.containsKey(productId)) {
            purchasedProducts.put(productId, null);
        }

        // Return purchase request status
        return responseCode;
    }

    /**
     * This method is called when an update about a purchased product is received. It logs the
     * update in local inventory.
     *
     * @param purchase the product which was purchased
     */
    private void onItemPurchased(Purchase purchase) {
        String orderId = purchase.getOrderId();
        String productId = purchase.getSku();

        if (!purchasedProducts.containsKey(productId) || purchasedProducts.get(productId) == null) {
            purchasedProducts.put(productId, orderId);
        }
    }

    /**
     * Checks if the user has purchased the specified product or not.
     *
     * @param productId Unique if of the product to check
     * @return Returns true if the product is owned by the user, false otherwise
     */
    public boolean hasPurchased(String productId) {
        return purchasedProducts.containsKey(productId);
    }

    /**
     * Checks if the client is currently connected to the service, so that requests to other methods
     * will succeed.
     *
     * @return Returns true if the client is currently connected to the service, false otherwise.
     */
    public boolean isReady() {
        return mBillingClient.isReady();
    }

    /**
     * This callback is fired when purchases are updated. Both purchases initiated by
     * your app and the ones initiated by Play Store will be reported here.
     *
     * @param responseCode Response code of the update.
     * @param purchases    List of updated purchases if present.
     */
    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                onItemPurchased(purchase);
            }
        }
    }

    /**
     * This callback is fired when configuration of the billing server is finished. If
     * a connection was successfully established, local inventory and purchase details
     * are updated.
     *
     * @param status this code indicates whether the configuration was successful
     *               or not
     */
    @Override
    public void onBillingSetupFinished(int status) {
        if (status == BillingClient.BillingResponse.OK) {
            syncInventory();
        }
    }

    /**
     * This callback is fired if connection to billing server is lost. It tries to
     * reconnect to the server after 5 seconds.
     */
    @Override
    public void onBillingServiceDisconnected() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBillingClient.startConnection(BillingManager.this);
            }
        }, 5000L);
    }

    public static class Products {
        public static final String AD_REMOVE = "doodle.once.adremove";
    }

}