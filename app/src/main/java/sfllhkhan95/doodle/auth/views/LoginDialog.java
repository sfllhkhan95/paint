package sfllhkhan95.doodle.auth.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.facebook.login.widget.LoginButton;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;
import sfllhkhan95.doodle.auth.utils.AuthHandler;
import sfllhkhan95.doodle.auth.utils.OnUpdateListener;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 01/11/2018 8:02 PM
 */
public class LoginDialog extends Dialog {

    @NonNull
    private AuthHandler mAuthHandler;

    public LoginDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        mAuthHandler = new AuthHandler(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);

        findViewById(R.id.facebookSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.loginButton).performClick();
            }
        });

        findViewById(R.id.googleSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthHandler.signInWithGoogle();
            }
        });

        // Configure authentication
        if (!mAuthHandler.isSignedIn()) {
            mAuthHandler.signIn();
        }

        // Set up Facebook login button
        LoginButton mLoginButton = findViewById(R.id.loginButton);
        mAuthHandler.registerFacebookLoginButton(mLoginButton);
    }

    public void setOnUpdateListener(@NonNull OnUpdateListener onUpdateListener) {
        mAuthHandler.setOnUpdateListener(onUpdateListener);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mAuthHandler.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isAuthenticated() {
        return mAuthHandler.isSignedIn() && mAuthHandler.getCurrentUser() != null;
    }

    public void signOut() {
        mAuthHandler.signOut();
    }

    public void onDestroy() {
        mAuthHandler.stopTracking();
    }

    @Nullable
    public User getCurrentUser() {
        return mAuthHandler.getCurrentUser();
    }

}