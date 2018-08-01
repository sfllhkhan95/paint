package sfllhkhan95.doodle.auth.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import pk.aspirasoft.core.io.OnCompleteListener;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;
import sfllhkhan95.doodle.auth.utils.FacebookUserPhotoDownloader;

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
public class UserView implements OnCompleteListener<Bitmap> {

    private final Bitmap DEFAULT_AVATAR;

    private TextView mUserNameView;
    private TextView mUserEmailView;
    private ImageView mAvatarView;

    private TextView mJoinDateView;
    private TextView mLoginDateView;
    private TextView mLastBackupView;

    private Bitmap mUserAvatar;

    public UserView(Activity parent) {
        mUserNameView = parent.findViewById(R.id.headline);
        mUserEmailView = parent.findViewById(R.id.email);
        mJoinDateView = parent.findViewById(R.id.userSince);
        mLoginDateView = parent.findViewById(R.id.lastLoginDate);
        mLastBackupView = parent.findViewById(R.id.lastBackupDate);
        mAvatarView = parent.findViewById(R.id.userAvatar);

        DEFAULT_AVATAR = BitmapFactory.decodeResource(parent.getResources(), R.drawable.avatar_placeholder);
    }

    public UserView(Dialog parent) {
        mUserNameView = parent.findViewById(R.id.headline);
        mUserEmailView = parent.findViewById(R.id.email);
        mJoinDateView = parent.findViewById(R.id.userSince);
        mLoginDateView = parent.findViewById(R.id.lastLoginDate);
        mLastBackupView = parent.findViewById(R.id.lastBackupDate);
        mAvatarView = parent.findViewById(R.id.userAvatar);

        DEFAULT_AVATAR = BitmapFactory.decodeResource(parent.getContext().getResources(), R.drawable.avatar_placeholder);
    }

    public void showUser(User user) {
        mUserNameView.setText(user.getFirstName());
        mUserEmailView.setText(user.getEmail());
        mJoinDateView.setText(user.getCreationDate());
        mLoginDateView.setText(user.getLoginDate());
        mLastBackupView.setText(user.getBackupDate());
        if (user.getUid() == null || user.getUid().isEmpty()) {
            mAvatarView.setImageBitmap(DEFAULT_AVATAR);
        } else if (mUserAvatar != null) {
            onSuccess(mUserAvatar);
        } else {
            mAvatarView.setImageBitmap(DEFAULT_AVATAR);
            downloadUserPhoto(user.getUid(), 150, 150);
        }
    }

    private void downloadUserPhoto(String uid, int wd, int ht) {
        FacebookUserPhotoDownloader downloadTask = new FacebookUserPhotoDownloader(uid, wd, ht);
        downloadTask.setPhotoTracker(this);
        downloadTask.execute();
    }

    @Override
    public void onSuccess(@NonNull Bitmap userAvatar) {
        mUserAvatar = userAvatar;
        mAvatarView.setImageBitmap(userAvatar);
    }

    @Override
    public void onFailure(@NonNull Exception ex) {

    }

}