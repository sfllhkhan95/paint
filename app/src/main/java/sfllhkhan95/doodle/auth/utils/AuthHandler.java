package sfllhkhan95.doodle.auth.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pk.aspirasoft.core.db.PersistentValue;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;

public class AuthHandler implements FacebookCallback<LoginResult>, OnCompleteListener<AuthResult> {

    private static final String CURRENT_USER = "active_user";

    private final Context context;

    private final CallbackManager callbackManager;
    private final FBProfileTracker profileTracker;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;
    private Profile fbProfile;

    private PersistentValue<User> persistentUser;
    private User currentUser;

    private OnUpdateListener onUpdateListener;

    public AuthHandler(Context context) {
        this.context = context;

        // Configure Facebook authentication parameters
        callbackManager = CallbackManager.Factory.create();
        profileTracker = new FBProfileTracker();
        profileTracker.startTracking();

        persistentUser = new PersistentValue<>(CURRENT_USER, User.class);
        currentUser = persistentUser.getValue();
    }

    public void signIn() {
        if (!isSignedInToFirebase()) {
            signInToFirebase();
        } else {
            firebaseUser = firebaseAuth.getCurrentUser();
            onFirebaseSignedIn(firebaseUser);
        }
    }

    private void signOut() {
        firebaseAuth.signOut();
        currentUser = null;
        persistentUser.setValue(null);

        if (onUpdateListener != null) {
            onUpdateListener.onUpdate();
        }
    }

    public boolean isSignedIn() {
        return Profile.getCurrentProfile() != null && currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void registerFacebookLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, this);
    }

    private boolean isSignedInToFirebase() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void signInToFirebase() {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            AuthResult authResult = task.getResult();
            firebaseUser = authResult.getUser();
            onFirebaseSignedIn(firebaseUser);
        }
    }

    private void onFirebaseSignedIn(FirebaseUser firebaseUser) {
        if (currentUser == null) {
            currentUser = User.from(firebaseUser);
        } else {
            currentUser.updateWith(firebaseUser);
        }

        persistentUser.setValue(currentUser);
        if (onUpdateListener != null) {
            onUpdateListener.onUpdate();
        }
    }

    /**
     * This callback is triggered when Facebook authentication is successful
     */
    @Override
    public void onSuccess(LoginResult loginResult) {
        Toast.makeText(context, R.string.signed_in, Toast.LENGTH_SHORT).show();
        linkFacebookAndFirebase(loginResult.getAccessToken());

        fbProfile = Profile.getCurrentProfile();
        onFacebookSignedIn(fbProfile);
    }

    @Override
    public void onCancel() {
        Toast.makeText(context, R.string.error_sign_in_cancelled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
    }

    private void onFacebookSignedIn(Profile fbProfile) {
        if (currentUser == null) {
            currentUser = User.from(fbProfile);
        } else {
            currentUser.updateWith(fbProfile);
        }

        persistentUser.setValue(currentUser);
        if (onUpdateListener != null) {
            onUpdateListener.onUpdate();
        }
    }

    /**
     * Links user's Facebook and Firebase accounts.
     */
    private void linkFacebookAndFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // If Firebase account is already authenticated, link Facebook credentials with
        // the same user account
        if (isSignedInToFirebase()) {
            firebaseUser.linkWithCredential(credential).addOnCompleteListener(this);
        }

        // If no Firebase user is authenticated, sign up using Facebook credentials
        else {
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this);
        }
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void stopTracking() {
        profileTracker.stopTracking();
    }

    private class FBProfileTracker extends ProfileTracker {
        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            fbProfile = currentProfile;
            if (fbProfile == null) {
                signOut();
            } else {
                onFacebookSignedIn(fbProfile);
            }
        }
    }
}