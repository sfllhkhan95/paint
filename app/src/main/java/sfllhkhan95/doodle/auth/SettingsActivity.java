package sfllhkhan95.doodle.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;

import sfllhkhan95.doodle.FAQsActivity;
import sfllhkhan95.doodle.PrivacyPolicy;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;
import sfllhkhan95.doodle.auth.utils.AuthHandler;
import sfllhkhan95.doodle.auth.utils.OnUpdateListener;
import sfllhkhan95.doodle.auth.views.UserView;
import sfllhkhan95.doodle.core.utils.DialogFactory;

/**
 * @author saifkhichi96
 * @version 1.0
 *          created on 23/10/2017 2:28 AM
 */
public class SettingsActivity extends AppCompatActivity implements OnUpdateListener,
        View.OnClickListener {

    private final User DEFAULT_USER = new User();

    private AuthHandler mAuthHandler;

    private UserView mUserView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Configure action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.about_developer).setOnClickListener(this);
        findViewById(R.id.privacy_policy).setOnClickListener(this);
        findViewById(R.id.faqs).setOnClickListener(this);
        findViewById(R.id.facebookConnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuthHandler.isSignedIn()) {
                    ((TextView) findViewById(R.id.facebookConnectionStatus)).setText("Connecting ...");
                }

                findViewById(R.id.loginButton).performClick();
            }
        });

        // Assign views
        mUserView = new UserView(this);

        // Configure authentication
        mAuthHandler = new AuthHandler(this);
        mAuthHandler.setOnUpdateListener(this);
        if (!mAuthHandler.isSignedIn()) {
            mAuthHandler.signIn();
        }

        // Set up Facebook login button
        LoginButton mLoginButton = findViewById(R.id.loginButton);
        mAuthHandler.registerFacebookLoginButton(mLoginButton);

        // Display appropriate view
        onUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getAuthHandler() != null) {
            getAuthHandler().stopTracking();
        }
    }

    @Override
    public void onUpdate() {
        if (mAuthHandler.isSignedIn()) {
            mUserView.showUser(mAuthHandler.getCurrentUser());
            ((TextView) findViewById(R.id.facebookConnectionStatus)).setText("Connected");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mAuthHandler.getCurrentUser().getFirstName());
                getSupportActionBar().setSubtitle(mAuthHandler.getCurrentUser().getEmail());
            }

            showBackupSection();
        } else {
            mUserView.showUser(DEFAULT_USER);
            ((TextView) findViewById(R.id.facebookConnectionStatus)).setText("Not Connected");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Doodle");
                getSupportActionBar().setSubtitle("Not signed in");
            }

            hideBackupSection();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.privacy_policy:
                startActivity(new Intent(this, PrivacyPolicy.class));
                break;

            case R.id.faqs:
                startActivity(new Intent(this, FAQsActivity.class));
                break;

            case R.id.about_developer:
                new DialogFactory(this, null).
                        supportDialog(this).show();
                break;
        }
    }

    @Nullable
    public AuthHandler getAuthHandler() {
        return mAuthHandler;
    }

    private void showBackupSection() {
        findViewById(R.id.backupSectionInfo).setVisibility(View.GONE);
        findViewById(R.id.backupSectionBody).setVisibility(View.VISIBLE);
    }

    private void hideBackupSection() {
        findViewById(R.id.backupSectionInfo).setVisibility(View.VISIBLE);
        findViewById(R.id.backupSectionBody).setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            mAuthHandler.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException ignored) {
            //
        }
    }

}