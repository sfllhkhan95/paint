package sfllhkhan95.doodle.auth.models

import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*


class User {

    private val DEFAULT_FIRST_NAME = ""
    private val DEFAULT_LAST_NAME = ""
    private val DEFAULT_EMAIL = ""

    private val DEFAULT_CREATION_TIME: Long = 0
    private val DEFAULT_LOGIN_TIME: Long = 0
    private val DEFAULT_BACKUP_TIME: Long = 0

    var uid: String? = null
        private set
    var firstName = DEFAULT_FIRST_NAME
        private set
    var lastName = DEFAULT_LAST_NAME
        private set
    var email: String? = DEFAULT_EMAIL
        private set

    private var creationTime = DEFAULT_CREATION_TIME
    private var loginTime = DEFAULT_LOGIN_TIME
    private var backupTime = DEFAULT_BACKUP_TIME

    val creationDate: String
        get() {
            if (creationTime == DEFAULT_CREATION_TIME) {
                return "N/A"
            }

            val date = Date(creationTime)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            return format.format(date)
        }

    val loginDate: String
        get() {
            if (loginTime == DEFAULT_LOGIN_TIME) {
                return "N/A"
            }

            val date = Date(loginTime)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            return format.format(date)
        }

    val backupDate: String
        get() {
            if (backupTime == DEFAULT_BACKUP_TIME) {
                return "Never"
            }

            val date = Date(backupTime)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            return format.format(date)
        }

    fun updateWith(firebaseUser: FirebaseUser) {
        email = firebaseUser.email

        val metadata = firebaseUser.metadata
        if (metadata != null) {
            setCreationTime(metadata.creationTimestamp)
            setLoginTime(metadata.lastSignInTimestamp)
        }
    }

    fun updateWith(facebookProfile: Profile) {
        uid = "https://graph.facebook.com/" + facebookProfile.id +
                "/picture?width=150&height=150"
        firstName = facebookProfile.firstName
        lastName = facebookProfile.lastName
    }

    fun updateWith(googleSignInAccount: GoogleSignInAccount) {
        uid = googleSignInAccount.photoUrl.toString()
        if (googleSignInAccount.givenName != null)
            firstName = googleSignInAccount.givenName!!

        if (googleSignInAccount.familyName != null)
            lastName = googleSignInAccount.familyName!!
    }

    private fun setCreationTime(creationTime: Long) {
        this.creationTime = creationTime
    }

    private fun setLoginTime(loginTime: Long) {
        this.loginTime = loginTime
    }

    fun setBackupTime(backupTime: Long) {
        this.backupTime = backupTime
    }

    companion object {
        fun from(firebaseUser: FirebaseUser): User {
            val user = User()
            user.updateWith(firebaseUser)
            return user
        }

        fun from(facebookProfile: Profile): User {
            val user = User()
            user.updateWith(facebookProfile)
            return user
        }

        fun from(googleSignInAccount: GoogleSignInAccount): User {
            val user = User()
            user.updateWith(googleSignInAccount)
            return user
        }

        fun from(firebaseUser: FirebaseUser, facebookProfile: Profile): User {
            val user = User.from(facebookProfile)
            user.updateWith(firebaseUser)
            return user
        }
    }

}