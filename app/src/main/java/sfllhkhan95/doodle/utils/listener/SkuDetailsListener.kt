package sfllhkhan95.doodle.utils.listener

import com.android.billingclient.api.SkuDetails

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.4.0
 * created on 04/06/2018 1:04 AM
 */

interface SkuDetailsListener {

    fun onDetailsReceived(details: SkuDetails)

}
