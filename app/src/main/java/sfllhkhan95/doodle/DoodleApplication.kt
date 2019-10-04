package sfllhkhan95.doodle

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.orhanobut.hawk.Hawk
import io.fabric.sdk.android.Fabric
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.utils.LocaleUtils

/**
 * Doodle is the Application class which bootstraps everything and initializes the global
 * state of the app.
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
class DoodleApplication : Application() {

    /**
     * Called when the application is starting, before any other application objects
     * are created. Used for initial configuration.
     */
    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()

        // Disable crash reporting in DEBUG mode
        Fabric.with(this, Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build())

        // Initialize ad SDKs
        AdManager.initialize(this)

        // Restore locale settings
        LocaleUtils.restoreLocaleSettings(this)
    }

    companion object {
        // Actions
        const val ACTION_SHARE = "SHARE"

        // Defaults
        const val DEFAULT_NOTIFICATION_CHANNEL = "PRIMARY_CHANNEL"
        const val DEFAULT_PROJECT_NAME = "Untitled"
        const val EXT_IMAGE = ".jpg"
        const val EXT_IMAGE_MIME = "image/jpeg"
        const val EXT_METADATA = ".json"

        // Events
        const val EVENT_PROJECT_CREATE = "from_device"
        const val EVENT_PROJECT_CREATE_BLANK = "from_scratch"

        // Properties
        const val PROPERTY_SUCCESS = "success"

        // Flags
        const val FLAG_INTRO = "INTRO_SEEN"
        const val FLAG_READ_ONLY = "READ_ONLY"

        // Shared file names
        const val FILE_CROPPED = "CROPPED"
        const val FILE_CAMERA = "CAMERA_IMAGE"
        const val FILE_SHAREABLE = "SHARE_IMAGE"

        // Project properties
        const val PROJECT_FROM_IMAGE = "FROM_DEVICE"
        const val PROJECT_FROM_SAVED = "DOODLE"

        // Request codes
        const val REQUEST_ALL_PERMISSIONS = 1
        const val REQUEST_PHOTO_CAPTURE = 100
        const val REQUEST_PHOTO_PICK = 200
        const val REQUEST_SHARE_DOODLE = 300

        // Tags
        const val TAG_NOTIFICATIONS = "NOTIFICATIONS"
        const val TAG_THEME = "APP_THEME"
    }

}