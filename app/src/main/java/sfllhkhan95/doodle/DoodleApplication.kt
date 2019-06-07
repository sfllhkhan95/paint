package sfllhkhan95.doodle

import android.app.Application
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.orhanobut.hawk.Hawk
import io.fabric.sdk.android.Fabric
import sfllhkhan95.doodle.ads.AdManager
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_CHOCOLATE
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DARK
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DEFAULT
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_FOREST
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_OCEAN
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_SUNLIGHT

/**
 * Doodle is the Application class which bootstraps everything and initializes the global
 * state of the app.
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
class DoodleApplication : Application() {

    val currentTheme: String
        get() {
            var currentTheme: String? = Hawk.get(THEME, null)
            if (currentTheme == null) currentTheme = THEME_DEFAULT
            return currentTheme
        }

    fun getDialogTheme(): Int {
        return when (currentTheme) {
            THEME_OCEAN -> R.style.DialogTheme_Ocean
            THEME_SUNLIGHT -> R.style.DialogTheme_Sunlight
            THEME_FOREST -> R.style.DialogTheme_Forest
            THEME_CHOCOLATE -> R.style.DialogTheme_Chocolate
            THEME_DARK -> R.style.DialogTheme_Dark
            THEME_DEFAULT -> R.style.DialogTheme
            else -> R.style.DialogTheme
        }
    }

    fun setActivityTheme(activity: AppCompatActivity): Int {
        when (currentTheme) {
            THEME_OCEAN -> {
                activity.setTheme(R.style.AppTheme_Ocean)
                return 1
            }
            THEME_SUNLIGHT -> {
                activity.setTheme(R.style.AppTheme_Sunlight)
                return 2
            }
            THEME_FOREST -> {
                activity.setTheme(R.style.AppTheme_Forest)
                return 3
            }
            THEME_CHOCOLATE -> {
                activity.setTheme(R.style.AppTheme_Chocolate)
                return 4
            }
            THEME_DARK -> {
                activity.setTheme(R.style.AppTheme_Dark)
                return 5
            }
            THEME_DEFAULT -> {
                activity.setTheme(R.style.AppTheme)
                return 0
            }
            else -> {
                activity.setTheme(R.style.AppTheme)
                return 0
            }
        }
    }

    fun changeTheme(activity: AppCompatActivity, currentTheme: String) {
        when (currentTheme) {
            THEME_OCEAN -> Hawk.put(THEME, THEME_OCEAN)
            THEME_SUNLIGHT -> Hawk.put(THEME, THEME_SUNLIGHT)
            THEME_FOREST -> Hawk.put(THEME, THEME_FOREST)
            THEME_CHOCOLATE -> Hawk.put(THEME, THEME_CHOCOLATE)
            THEME_DARK -> Hawk.put(THEME, THEME_DARK)
            THEME_DEFAULT -> Hawk.put(THEME, THEME_DEFAULT)
            else -> Hawk.put(THEME, THEME_DEFAULT)
        }
        activity.recreate()
    }

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
    }

    companion object {
        const val TAG = "DoodleLogs"
        const val INTRO = "INTRO_SEEN"
        private const val THEME = "APP_THEME"
    }

}