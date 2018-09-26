package sfllhkhan95.doodle.auth.utils

import android.content.Context
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pk.aspirasoft.core.db.PersistentValue
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.auth.models.User

class AuthHandler(private val context: Context) : FacebookCallback<LoginResult>, OnCompleteListener<AuthResult> {

    val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val profileTracker: FBProfileTracker
    private val firebaseAuth = FirebaseAuth.getInstance()

    private var firebaseUser: FirebaseUser? = null
    private var fbProfile: Profile? = null

    private val persistentUser: PersistentValue<User>
    var currentUser: User? = null
        private set

    private var onUpdateListener: OnUpdateListener? = null

    val isSignedIn: Boolean
        get() = Profile.getCurrentProfile() != null && currentUser != null

    private val isSignedInToFirebase: Boolean
        get() = firebaseAuth.currentUser != null

    init {
        // Configure Facebook authentication parameters
        profileTracker = FBProfileTracker()
        profileTracker.startTracking()

        persistentUser = PersistentValue(CURRENT_USER, User::class.java)
        currentUser = persistentUser.value
    }

    fun signIn() {
        if (!isSignedInToFirebase) {
            signInToFirebase()
        } else {
            firebaseUser = firebaseAuth.currentUser
            onFirebaseSignedIn(firebaseUser)
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        currentUser = null
        persistentUser.value = null

        if (onUpdateListener != null) {
            onUpdateListener!!.onUpdate()
        }
    }

    fun registerFacebookLoginButton(loginButton: LoginButton) {
        loginButton.setReadPermissions("email")
        loginButton.registerCallback(callbackManager, this)
    }

    private fun signInToFirebase() {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this)
    }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val authResult = task.result
            firebaseUser = authResult.user
            onFirebaseSignedIn(firebaseUser)
        }
    }

    private fun onFirebaseSignedIn(firebaseUser: FirebaseUser?) {
        if (currentUser == null) {
            currentUser = User.from(firebaseUser!!)
        } else {
            currentUser!!.updateWith(firebaseUser!!)
        }

        persistentUser.value = currentUser
        if (onUpdateListener != null) {
            onUpdateListener!!.onUpdate()
        }
    }

    /**
     * This callback is triggered when Facebook authentication is successful
     */
    override fun onSuccess(loginResult: LoginResult) {
        Toast.makeText(context, R.string.signed_in, Toast.LENGTH_SHORT).show()
        linkFacebookAndFirebase(loginResult.accessToken)

        fbProfile = Profile.getCurrentProfile()
        onFacebookSignedIn(fbProfile)
    }

    override fun onCancel() {
        Toast.makeText(context, R.string.error_sign_in_cancelled, Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: FacebookException) {
        Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
    }

    private fun onFacebookSignedIn(fbProfile: Profile?) {
        if (currentUser == null) {
            currentUser = User.from(fbProfile!!)
        } else {
            currentUser!!.updateWith(fbProfile!!)
        }

        persistentUser.value = currentUser
        if (onUpdateListener != null) {
            onUpdateListener!!.onUpdate()
        }
    }

    /**
     * Links user's Facebook and Firebase accounts.
     */
    private fun linkFacebookAndFirebase(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)

        // If Firebase account is already authenticated, link Facebook credentials with
        // the same user account
        if (isSignedInToFirebase) {
            firebaseUser!!.linkWithCredential(credential).addOnCompleteListener(this)
        } else {
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this)
        }// If no Firebase user is authenticated, sign up using Facebook credentials
    }

    fun setOnUpdateListener(onUpdateListener: OnUpdateListener) {
        this.onUpdateListener = onUpdateListener
    }

    fun stopTracking() {
        profileTracker.stopTracking()
    }

    private inner class FBProfileTracker : ProfileTracker() {
        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
            fbProfile = currentProfile
            if (fbProfile == null) {
                signOut()
            } else {
                onFacebookSignedIn(fbProfile)
            }
        }
    }

    companion object {
        private const val CURRENT_USER = "active_user"
    }
}