package sfllhkhan95.doodle.views.activity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:26 AM
 */
class PrivacyPolicy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this)
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.loadUrl(resources.getString(R.string.privacy_policy_url))
        setContentView(webView)
    }

}