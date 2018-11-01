package sfllhkhan95.doodle.auth.utils

import android.app.Activity
import android.content.Intent
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
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.auth.models.User

class AuthHandler(private val context: Activity) : FacebookCallback<LoginResult>, OnCompleteListener<AuthResult> {

    val RC_SIGN_IN = 50;
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
        get() = (GoogleSignIn.getLastSignedInAccount(context) != null || Profile.getCurrentProfile() != null)
                && currentUser != null

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

    public fun signOut() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("565824658688-kqhlc2kr162966m02j005vkhr3qpcvm3.apps.googleusercontent.com")
                .requestEmail()
                .build()
        GoogleSignIn.getClient(context, gso).signOut()
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

    var credential: AuthCredential? = null

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val authResult = task.result
            firebaseUser = authResult?.user
            onFirebaseSignedIn(firebaseUser)
        } else {
            val prevUser = FirebaseAuth.getInstance().currentUser
            if (credential != null) {
                firebaseAuth.signInWithCredential(credential!!)
                        .addOnSuccessListener { result ->
                            val currentUser = result.user
                            // TODO: Merge prevUser and currentUser accounts and data
                        }
                        .addOnFailureListener {
                            // ...
                        }
            }
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

    private fun onGoogleSignedIn(account: GoogleSignInAccount?) {
        if (currentUser == null) {
            currentUser = User.from(account!!)
        } else {
            currentUser!!.updateWith(account!!)
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
        credential = FacebookAuthProvider.getCredential(token.token)

        // If Firebase account is already authenticated, link Facebook credentials with
        // the same user account
        if (isSignedInToFirebase) {
            firebaseUser!!.linkWithCredential(credential!!).addOnCompleteListener(this)
        }

        // If no Firebase user is authenticated, sign up using Facebook credentials
        else {
            firebaseAuth.signInWithCredential(credential!!).addOnCompleteListener(this)
        }
    }

    fun signInWithGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("565824658688-kqhlc2kr162966m02j005vkhr3qpcvm3.apps.googleusercontent.com")
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent
        context.startActivityForResult(signInIntent, RC_SIGN_IN)
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

    private fun linkGoogleAndFacebook(acct: GoogleSignInAccount) {
        credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        // If Firebase account is already authenticated, link Google credentials with
        // the same user account
        if (isSignedInToFirebase) {
            firebaseUser!!.linkWithCredential(credential!!).addOnCompleteListener(this)
        }

        // If no Firebase user is authenticated, sign up using Google credentials
        else {
            firebaseAuth.signInWithCredential(credential!!).addOnCompleteListener(this)
        }
    }

    fun setOnUpdateListener(onUpdateListener: OnUpdateListener) {
        this.onUpdateListener = onUpdateListener
    }

    fun stopTracking() {
        profileTracker.stopTracking()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                linkGoogleAndFacebook(account!!)

                onGoogleSignedIn(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }
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