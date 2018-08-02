package sfllhkhan95.doodle.auth.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pk.aspirasoft.core.io.OnCompleteListener;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;
import sfllhkhan95.doodle.auth.utils.FacebookUserPhotoDownloader;

/**
 * @author saifkhichi96
 * @version 2.0.0
 * created on 23/10/2017 2:28 AM
 */
public class UserView extends View implements OnCompleteListener<Bitmap> {

    private final Bitmap DEFAULT_AVATAR;

    private TextView mUserNameView;
    private TextView mUserEmailView;
    private ImageView mAvatarView;

    private static Bitmap mUserAvatar;
    private static boolean isDownloading;

    public UserView(Context context) {
        super(context);
        DEFAULT_AVATAR = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_placeholder);
    }

    public UserView setNameView(TextView mUserNameView) {
        this.mUserNameView = mUserNameView;
        return this;
    }

    public UserView setEmailView(TextView mUserEmailView) {
        this.mUserEmailView = mUserEmailView;
        return this;
    }

    public UserView setAvatarView(ImageView mAvatarView) {
        this.mAvatarView = mAvatarView;
        return this;
    }

    private void showName(String name) {
        if (mUserNameView != null && name != null) {
            mUserNameView.setText(name);
        }
    }

    private void showEmail(String email) {
        if (mUserEmailView != null && email != null) {
            mUserEmailView.setText(email.split("@")[0]);
        }
    }

    private void showAvatar(Bitmap avatar) {
        if (mAvatarView != null && avatar != null) {
            mAvatarView.setImageBitmap(avatar);
        }
    }

    public void showUser(@NonNull User user) {
        showName(user.getFirstName());
        showEmail(user.getEmail());
        if (user.getUid() == null || user.getUid().isEmpty()) {
            showAvatar(DEFAULT_AVATAR);
        } else if (mUserAvatar != null) {
            onSuccess(mUserAvatar);
        } else {
            showAvatar(DEFAULT_AVATAR);
            downloadUserPhoto(user.getUid(), 150, 150);
        }
    }

    private void downloadUserPhoto(String uid, int wd, int ht) {
        if (!isDownloading) {
            FacebookUserPhotoDownloader downloadTask = new FacebookUserPhotoDownloader(uid, wd, ht);
            downloadTask.setPhotoTracker(this);
            downloadTask.execute();

            isDownloading = true;
        }
    }

    @Override
    public void onSuccess(@NonNull Bitmap userAvatar) {
        mUserAvatar = userAvatar;
        showAvatar(userAvatar);

        isDownloading = false;
    }

    @Override
    public void onFailure(@NonNull Exception ex) {
        isDownloading = false;
    }
}