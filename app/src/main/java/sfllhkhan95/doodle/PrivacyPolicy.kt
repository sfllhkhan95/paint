package sfllhkhan95.doodle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:26 AM
 */
class PrivacyPolicy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.loadUrl(resources.getString(R.string.privacy_policy_url))
        setContentView(webView)
    }

}