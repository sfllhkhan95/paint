package sfllhkhan95.doodle.auth.views

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.facebook.messenger.MessengerUtils
import com.facebook.messenger.ShareToMessengerParams
import sfllhkhan95.doodle.R

/**
 * A button which can be used to share content to Messenger.
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 * @see [Messenger](https://www.messenger.com/features)
 */
class MessengerShareButton : RelativeLayout {

    private var mActionText: String? = null
    private var mDescriptionText: String? = null

    private var mActionStringView: TextView? = null
    private var mDescriptionStringView: TextView? = null

    private var mActionButton: View? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        View.inflate(context, R.layout.messenger_share_button, this)

        mActionStringView = findViewById(R.id.action)
        mDescriptionStringView = findViewById(R.id.recipient)
        mActionButton = findViewById(R.id.share_button)

        val DEFAULT_ACTION_TEXT = context.getString(R.string.label_compose)
        setActionText(DEFAULT_ACTION_TEXT)

        val DEFAULT_DESCRIPTION_TEXT = context.getString(R.string.label_new_message)
        setDescriptionText(DEFAULT_DESCRIPTION_TEXT)

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.MessengerShareButton,
                    defStyleAttr, 0)
            try {
                mActionText = a.getString(R.styleable.MessengerShareButton_actionText)
                mDescriptionText = a.getString(R.styleable.MessengerShareButton_descriptionText)
            } finally {
                a.recycle()

                setActionText((if (mActionText == null) DEFAULT_ACTION_TEXT else mActionText)!!)
                setDescriptionText((if (mDescriptionText == null) DEFAULT_DESCRIPTION_TEXT else mDescriptionText)!!)
            }
        }
    }

    fun setActionText(actionText: String) {
        this.mActionText = actionText
        this.mActionStringView!!.text = actionText
        invalidate()
        requestLayout()
    }

    fun setDescriptionText(descriptionText: String) {
        this.mDescriptionText = descriptionText
        this.mDescriptionStringView!!.text = mDescriptionText
        invalidate()
        requestLayout()
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        mActionButton!!.setOnClickListener(l)
        mActionStringView!!.setOnClickListener(l)
        mDescriptionStringView!!.setOnClickListener(l)
    }

    /**
     * Sends content back to the active conversation in Messenger from which a message was
     * received. If Messenger app is not installed, user would be redirected to the download
     * page in the Play Store.
     *
     * @param requestFrom the Activity which is sharing content
     * @param contentType the MIME type of the content being shared
     * @param contentUri  contentUri points to the content being shared to Messenger
     */
    fun sendReply(requestFrom: Activity, contentType: String, contentUri: Uri) {
        val shareParams = ShareToMessengerParams.newBuilder(contentUri, contentType)
                .build()

        MessengerUtils.finishShareToMessenger(requestFrom, shareParams)
    }

    /**
     * Opens Messenger's native flow for sharing content in a new conversation on Messenger.
     * If Messenger app is not installed, user would be redirected to the download page in
     * the Play Store.
     *
     * @param requestFrom the Activity which is sharing content
     * @param contentType the MIME type of the content being shared
     * @param contentUri  contentUri points to the content being shared to Messenger
     * @param requestCode this request code can be used to perform action on request completion
     * in the requesting activity's Activity#onActivityResult method
     */
    fun sendMessage(requestFrom: Activity, contentType: String,
                    contentUri: Uri, requestCode: Int) {
        val shareParams = ShareToMessengerParams.newBuilder(contentUri, contentType)
                .build()

        MessengerUtils.shareToMessenger(
                requestFrom,
                requestCode,
                shareParams)
    }

}