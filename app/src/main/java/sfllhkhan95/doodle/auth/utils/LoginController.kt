package sfllhkhan95.doodle.auth.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import pk.aspirasoft.core.db.PersistentValue
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.auth.models.User
import sfllhkhan95.doodle.auth.views.LoginView
import sfllhkhan95.doodle.core.utils.OnUpdateListener


class LoginController(private val context: Activity, themeResId: Int) :
        Dialog(context, themeResId), OnCompleteListener<AuthResult> {

    private var mFacebookClient: FacebookClient? = null
    private var mGoogleClient: GoogleClient = GoogleClient(this)
    private var mEmailClient: EmailClient = EmailClient(this)

    private var loginView: LoginView? = null

    // Doodle auth params
    private val persistentUser: PersistentValue<User> = PersistentValue(CURRENT_USER, User::class.java)
    var currentUser: User? = persistentUser.value
        private set(value) {
            field = value
            persistentUser.value = value
            onUpdateListener?.onUpdate()
        }

    var onUpdateListener: OnUpdateListener? = null

    val isSignedIn: Boolean
        get() = currentUser != null

    init {
        if (!this.isSignedIn) {
            FirebaseAuth.getInstance().currentUser?.let {
                onSignedIn(it, null, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)
        loginView = LoginView(this)

        mFacebookClient = FacebookClient(this, findViewById(R.id.loginButton))

        // Register event handlers
        findViewById<View>(R.id.facebookSignInButton)?.setOnClickListener { mFacebookClient?.signIn() }
        findViewById<View>(R.id.googleSignInButton)?.setOnClickListener { mGoogleClient.signIn() }
        findViewById<View>(R.id.signInButton)?.setOnClickListener { mEmailClient.signIn() }
        findViewById<View>(R.id.resetButton)?.setOnClickListener { mEmailClient.resetPassword() }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookClient?.onComplete(requestCode, resultCode, data)
        mGoogleClient.onComplete(requestCode, resultCode, data)
    }

    private fun signInWithCredential(credential: AuthCredential?) {
        Log.d(DoodleApplication.TAG, "signInWithCredential()")
        credential?.let { authCredential ->
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(authCredential)
                    ?.addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                Log.d(DoodleApplication.TAG, "linkWithCredential()::success")
                            }
                            else -> {
                                Log.e(DoodleApplication.TAG, "linkWithCredential()::"
                                        + (task.exception?.message ?: "failure"))
                            }
                        }
                    }
                    ?: FirebaseAuth.getInstance().signInWithCredential(authCredential)
                            .addOnCompleteListener { task ->
                                when {
                                    task.isSuccessful -> {
                                        Log.d(DoodleApplication.TAG, "signInWithCredential()::success")
                                    }
                                    else -> {
                                        Log.e(DoodleApplication.TAG, "signInWithCredential()::"
                                                + (task.exception?.message ?: "failure"))
                                    }
                                }
                            }
        }
    }

    private fun onSignedIn(user: FirebaseUser?, profile: Profile?, account: GoogleSignInAccount?) {
        Log.d(DoodleApplication.TAG, "onSignedIn()")
        currentUser = when (currentUser) {
            null -> {
                user?.let { currentUser = User.from(user) }
                profile?.let { currentUser = User.from(profile) }
                account?.let { currentUser = User.from(account) }
                currentUser
            }
            else -> {
                user?.let { currentUser?.updateWith(it) }
                profile?.let { currentUser?.updateWith(it) }
                account?.let { currentUser?.updateWith(it) }
                currentUser
            }
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        Log.d(DoodleApplication.TAG, "onComplete()")
        when {
            task.isSuccessful -> task.result?.let { onSuccess(it) } ?: onError("Sign in failed")
            else -> onError("Sign in failed")
        }
    }

    private fun onSuccess(result: AuthResult) {
        Log.d(DoodleApplication.TAG, "onSuccess()")
        result.user?.let {
            onSignedIn(it, null, null)
        } ?: onError("Sign in failed")
    }

    private fun onError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
        Log.d(DoodleApplication.TAG, "signOut()")
        mGoogleClient?.signOut()
        mFacebookClient?.signOut()
        mEmailClient?.signOut()

        currentUser = null
    }

    fun onDestroy() {
        mFacebookClient?.stopTracking()
    }

    class EmailClient(private val controller: LoginController) {
        fun signIn() {
            Log.d(DoodleApplication.TAG, "EmailClient::signIn()")
            controller.loginView?.let { modelView ->
                modelView.hideErrors()
                when {
                    modelView.email.isEmpty() -> modelView.emailError = "This field is required"
                    modelView.password.isEmpty() -> modelView.passwordError = "This field is required"
                    else -> {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(modelView.email, modelView.password)
                                .addOnCompleteListener(controller)
                    }
                }
            }
        }

        fun resetPassword() {
            Log.d(DoodleApplication.TAG, "EmailClient::resetPassword()")
            controller.loginView?.let { modelView ->
                modelView.hideErrors()
                when {
                    modelView.email.isEmpty() -> modelView.emailError = "This field is required"
                    else -> {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(modelView.email, modelView.email)
                                .addOnSuccessListener {
                                    it.user?.delete()
                                    Toast.makeText(controller.context,
                                            "This email is not associated with any accounts",
                                            Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { _ ->
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(modelView.email)
                                            .addOnCompleteListener {
                                                when {
                                                    it.isSuccessful ->
                                                        Toast.makeText(controller.context,
                                                                "Check your email for further instructions",
                                                                Toast.LENGTH_SHORT).show()
                                                    else ->
                                                        Toast.makeText(controller.context,
                                                                "This email is not associated with any accounts",
                                                                Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                }
                    }
                }
            }

        }

        fun signOut() {
            Log.d(DoodleApplication.TAG, "EmailClient::signOut()")
            FirebaseAuth.getInstance().signOut()
        }
    }

    class GoogleClient(private val controller: LoginController) {
        private val mGoogleClient = GoogleSignIn.getClient(controller.context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("565824658688-kqhlc2kr162966m02j005vkhr3qpcvm3.apps.googleusercontent.com")
                        .requestEmail()
                        .build())

        fun signIn() {
            Log.d(DoodleApplication.TAG, "GoogleClient::signIn()")
            controller.context.startActivityForResult(mGoogleClient.signInIntent, RC_GOOGLE_SIGN_IN)
        }

        fun signOut() {
            Log.d(DoodleApplication.TAG, "GoogleClient::signIn()")
            mGoogleClient.signOut()
        }

        fun onComplete(requestCode: Int, resultCode: Int, data: Intent?) {
            when (requestCode) {
                RC_GOOGLE_SIGN_IN -> if (resultCode == RESULT_OK) {
                    try {
                        Log.d(DoodleApplication.TAG, "GoogleClient::onComplete()::success")
                        onSuccess(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java))

                    } catch (ex: ApiException) {
                        Log.d(DoodleApplication.TAG, "GoogleClient::onComplete()::" +
                                (ex.message ?: "failure"))
                        controller.onError(ex.message ?: "Sign in failed ")

                    }
                } else {
                    Log.d(DoodleApplication.TAG, "GoogleClient::onComplete()::failure")
                    controller.onError("Sign in failed")
                }
            }
        }

        private fun onSuccess(result: GoogleSignInAccount?) {
            Log.d(DoodleApplication.TAG, "GoogleClient::onSuccess()")
            result?.let {
                controller.signInWithCredential(GoogleAuthProvider.getCredential(it.idToken, null))
                controller.onSignedIn(null, null, it)
            }
        }
    }

    class FacebookClient(private val controller: LoginController, private val loginButton: LoginButton) : FacebookCallback<LoginResult> {
        private val mFacebookClient = LoginManager.getInstance()
        private val callbackManager: CallbackManager = CallbackManager.Factory.create()
        private val profileTracker: FBProfileTracker = FBProfileTracker()

        init {
            loginButton.setReadPermissions("email")
            loginButton.registerCallback(callbackManager, this)

            profileTracker.startTracking()
        }

        fun signIn() {
            Log.d(DoodleApplication.TAG, "FacebookClient::signIn()")
            loginButton.performClick()
        }

        fun signOut() {
            Log.d(DoodleApplication.TAG, "FacebookClient::signOut()")
            mFacebookClient.logOut()
        }

        fun stopTracking() {
            profileTracker.stopTracking()
        }

        fun onComplete(requestCode: Int, resultCode: Int, data: Intent?) {
            Log.d(DoodleApplication.TAG, "FacebookClient::onComplete()")
            this.callbackManager.onActivityResult(requestCode, resultCode, data)
        }

        override fun onSuccess(loginResult: LoginResult) {
            Log.d(DoodleApplication.TAG, "FacebookClient::onSuccess()")
            Profile.getCurrentProfile()?.let {
                controller.signInWithCredential(FacebookAuthProvider.getCredential(loginResult.accessToken.token))
                controller.onSignedIn(null, it, null)
            }
        }

        override fun onCancel() {
            Log.e(DoodleApplication.TAG, "FacebookClient::onCancel()")
            controller.onError(controller.context.getString(R.string.error_sign_in_cancelled))
        }

        override fun onError(error: FacebookException) {
            Log.e(DoodleApplication.TAG, "FacebookClient::onError()::"
                    + (error.message ?: "Sign in failed"))
            controller.onError(error.message ?: "Sign in failed")
        }

        private inner class FBProfileTracker : ProfileTracker() {
            override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                when (currentProfile) {
                    null -> controller.signOut()
                    else -> controller.onSignedIn(null, currentProfile, null)
                }
            }
        }
    }

    companion object {
        private const val CURRENT_USER = "active_user"
        private const val RC_GOOGLE_SIGN_IN = 50
    }
}