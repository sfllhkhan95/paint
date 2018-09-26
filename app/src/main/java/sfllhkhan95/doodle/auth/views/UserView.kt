package sfllhkhan95.doodle.auth.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.auth.models.User

/**
 * @author saifkhichi96
 * @version 3.0.0
 * created on 23/10/2017 2:28 AM
 */
class UserView(context: Context) : View(context) {

    private val DEFAULT_AVATAR: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_placeholder)

    private var mUserNameView: TextView? = null
    private var mUserEmailView: TextView? = null
    private var mAvatarView: ImageView? = null

    fun setNameView(mUserNameView: TextView): UserView {
        this.mUserNameView = mUserNameView
        return this
    }

    fun setEmailView(mUserEmailView: TextView): UserView {
        this.mUserEmailView = mUserEmailView
        return this
    }

    fun setAvatarView(mAvatarView: ImageView): UserView {
        this.mAvatarView = mAvatarView
        return this
    }

    private fun showName(name: String?) {
        if (mUserNameView != null && name != null && !name.isEmpty()) {
            mUserNameView?.text = name
        }
    }

    private fun showEmail(email: String?) {
        if (mUserEmailView != null && email != null && !email.isEmpty()) {
            mUserEmailView?.text = email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
    }

    private fun showAvatar(avatar: Bitmap?) {
        avatar?.let {
            mAvatarView?.setImageBitmap(avatar)
        }
    }

    fun showUser(user: User?) {
        showName(user?.firstName)
        showEmail(user?.email)
        if (user?.uid == null || user.uid!!.isEmpty()) {
            showAvatar(DEFAULT_AVATAR)
        }

        downloadUserPhoto(user?.uid)
    }

    private fun downloadUserPhoto(uid: String?) {
        mAvatarView?.let {
            Glide.with(this)
                    .load("https://graph.facebook.com/" + uid +
                            "/picture?width=150&height=150")
                    .into(it)
        }
    }

}