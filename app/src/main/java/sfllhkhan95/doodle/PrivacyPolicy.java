package sfllhkhan95.doodle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:26 AM
 */
public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        webView.loadUrl(getResources().getString(R.string.privacy_policy_url));
        setContentView(webView);
    }

}