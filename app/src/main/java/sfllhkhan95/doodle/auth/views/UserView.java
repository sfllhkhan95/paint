package sfllhkhan95.doodle.auth.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.models.User;

/**
 * @author saifkhichi96
 * @version 3.0.0
 * created on 23/10/2017 2:28 AM
 */
public class UserView extends View {

    private final Bitmap DEFAULT_AVATAR;

    private TextView mUserNameView;
    private TextView mUserEmailView;
    private ImageView mAvatarView;

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
        if (mUserNameView != null && name != null && !name.isEmpty()) {
            mUserNameView.setText(name);
        }
    }

    private void showEmail(String email) {
        if (mUserEmailView != null && email != null && !email.isEmpty()) {
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
        }

        downloadUserPhoto(user.getUid());
    }

    private void downloadUserPhoto(String uid) {
        Glide.with(this)
                .load("https://graph.facebook.com/" + uid +
                        "/picture?width=150&height=150")
                .into(mAvatarView);
    }

}