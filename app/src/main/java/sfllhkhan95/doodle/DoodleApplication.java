package sfllhkhan95.doodle;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.Fabric;
import pk.aspirasoft.core.db.PersistentStorage;
import sfllhkhan95.doodle.ads.AdManager;

import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DEFAULT;
import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_OCEAN;
import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_SUNLIGHT;

/**
 * Doodle is the Application class which bootstraps everything and initializes the global
 * state of the app.
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
public class DoodleApplication extends Application {

    public final static String TAG = "DoodleLogs";

    private final static String THEME = "APP_THEME";

    private String getCurrentTheme() {
        String currentTheme = PersistentStorage.get(THEME, String.class);
        if (currentTheme == null) currentTheme = THEME_DEFAULT;
        return currentTheme;
    }

    public int setActivityTheme(Activity activity) {
        switch (getCurrentTheme()) {
            case THEME_OCEAN:
                activity.setTheme(R.style.AppTheme_Ocean);
                return 1;
            case THEME_SUNLIGHT:
                activity.setTheme(R.style.AppTheme_Sunlight);
                return 2;
            case THEME_DEFAULT:
            default:
                activity.setTheme(R.style.AppTheme);
                return 0;
        }
    }

    public void changeTheme(Activity activity, @NonNull String currentTheme) {
        switch (currentTheme) {
            case THEME_OCEAN:
                PersistentStorage.put(THEME, THEME_OCEAN);
                break;
            case THEME_SUNLIGHT:
                PersistentStorage.put(THEME, THEME_SUNLIGHT);
                break;
            case THEME_DEFAULT:
            default:
                PersistentStorage.put(THEME, THEME_DEFAULT);
                break;
        }
        activity.recreate();
    }

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
        AdManager.Companion.initialize(this);
    }

}