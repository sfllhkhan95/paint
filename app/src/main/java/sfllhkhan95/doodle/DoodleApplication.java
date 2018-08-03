package sfllhkhan95.doodle;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.Fabric;
import pk.aspirasoft.core.db.PersistentStorage;
import sfllhkhan95.doodle.ads.AdManager;

/**
 * Doodle is the Application class which bootstraps everything and initializes the global
 * state of the app.
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
public class DoodleApplication extends Application {

    public static String TAG = "DoodleLogs";

    /**
     * Called when the application is starting, before any other application objects
     * are created. Used for initial configuration.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        PersistentStorage.init(this, "DDODLE_PREFS");

        // Disable crash reporting in DEBUG mode
        Crashlytics crashlytics = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build();
        Fabric.with(this, crashlytics);

        /* The following method call initializes the Facebook SDK, and is recommended to
         * be called as early as possible. The behavior of Facebook SDK functions is
         * undetermined if this function is not called.
         *
         * UPDATE SDK v4.19+: The Facebook SDK is now auto initialized on Application start. If you are
         * using the Facebook SDK in the main process and don't need a callback on SDK
         * initialization completion you can now remove calls to FacebookSDK.sdkInitialize.
         * If you do need a callback, you should manually invoke the callback in your code.
         * (i.e. the following line of code can be safely removed.) */
        FacebookSdk.sdkInitialize(getApplicationContext());

        /* Activating Facebook SDK's app event logging is required for the app to be
         * eligible for Facebook's App Review submission.
         *
         * UPDATE SDK v4.19+: It is automatically initialized unless disabled.
         * (i.e. the following line of code can be safely removed.) */
        AppEventsLogger.activateApp(this);

        // Initialize Ad SDK
        AdManager.initialize(this);
    }

}