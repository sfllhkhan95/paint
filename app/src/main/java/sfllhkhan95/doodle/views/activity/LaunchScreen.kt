package sfllhkhan95.doodle.views.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
import java.util.*

/**
 * @author saifkhichi96
 * @version 1.2.0
 * @since 1.0.0
 * created on 23/10/2017 2:26 AM
 */
class LaunchScreen : AppCompatActivity() {

    companion object {
        private const val delay = 1500L
    }

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val introSeen: Boolean = Hawk.get(DoodleApplication.FLAG_INTRO, false)
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(
                    Intent(
                        applicationContext,
                        if (!introSeen) IntroActivity::class.java else HomeActivity::class.java
                    )
                )
                finish()
            }
        }, delay)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        try {
            timer.cancel()
        } finally {
            super.onDestroy()
        }
    }

}