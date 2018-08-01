package sfllhkhan95.doodle.auth.models;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class User {

    private final String DEFAULT_FIRST_NAME = "Guest";
    private final String DEFAULT_LAST_NAME = "";
    private final String DEFAULT_EMAIL = "Not signed in";

    private final long DEFAULT_CREATION_TIME = 0;
    private final long DEFAULT_LOGIN_TIME = 0;
    private final long DEFAULT_BACKUP_TIME = 0;

    private String uid;
    private String firstName = DEFAULT_FIRST_NAME;
    private String lastName = DEFAULT_LAST_NAME;
    private String email = DEFAULT_EMAIL;

    private long creationTime = DEFAULT_CREATION_TIME;
    private long loginTime = DEFAULT_LOGIN_TIME;
    private long backupTime = DEFAULT_BACKUP_TIME;


    public static User from(FirebaseUser firebaseUser) {
        User user = new User();
        user.updateWith(firebaseUser);
        return user;
    }

    public static User from(Profile facebookProfile) {
        User user = new User();
        user.updateWith(facebookProfile);
        return user;
    }

    public static User from(FirebaseUser firebaseUser, Profile facebookProfile) {
        User user = User.from(facebookProfile);
        user.updateWith(firebaseUser);
        return user;
    }

    public void updateWith(FirebaseUser firebaseUser) {
        setEmail(firebaseUser.getEmail());

        FirebaseUserMetadata metadata = firebaseUser.getMetadata();
        if (metadata != null) {
            setCreationTime(metadata.getCreationTimestamp());
            setLoginTime(metadata.getLastSignInTimestamp());
        }
    }

    public void updateWith(Profile facebookProfile) {
        setUid(facebookProfile.getId());
        setFirstName(facebookProfile.getFirstName());
        setLastName(facebookProfile.getLastName());
    }

    public String getUid() {
        return uid;
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getCreationDate() {
        if (creationTime == DEFAULT_CREATION_TIME) {
            return "N/A";
        }

        Date date = new Date(creationTime);
        Format format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return format.format(date);
    }

    private void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getLoginDate() {
        if (loginTime == DEFAULT_LOGIN_TIME) {
            return "N/A";
        }

        Date date = new Date(loginTime);
        Format format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return format.format(date);
    }

    private void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getBackupDate() {
        if (backupTime == DEFAULT_BACKUP_TIME) {
            return "Never";
        }

        Date date = new Date(backupTime);
        Format format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return format.format(date);
    }

    public void setBackupTime(long backupTime) {
        this.backupTime = backupTime;
    }

}