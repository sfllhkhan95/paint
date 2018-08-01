package sfllhkhan95.doodle.auth.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;

import sfllhkhan95.doodle.R;

/**
 * A button which can be used to share content to Messenger.
 *
 * @see <a href="https://www.messenger.com/features">Messenger</a>
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
public class MessengerShareButton extends RelativeLayout {

    private String mActionText;
    private String mDescriptionText;

    private TextView mActionStringView;
    private TextView mDescriptionStringView;

    private View mActionButton;

    public MessengerShareButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MessengerShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MessengerShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.messenger_share_button, this);

        mActionStringView = findViewById(R.id.action);
        mDescriptionStringView = findViewById(R.id.recipient);
        mActionButton = findViewById(R.id.share_button);

        String DEFAULT_ACTION_TEXT = "Compose";
        setActionText(DEFAULT_ACTION_TEXT);

        String DEFAULT_DESCRIPTION_TEXT = "New Message";
        setDescriptionText(DEFAULT_DESCRIPTION_TEXT);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.MessengerShareButton,
                    defStyleAttr, 0);
            try {
                mActionText = a.getString(R.styleable.MessengerShareButton_actionText);
                mDescriptionText = a.getString(R.styleable.MessengerShareButton_descriptionText);
            } finally {
                a.recycle();

                setActionText(mActionText == null ? DEFAULT_ACTION_TEXT : mActionText);
                setDescriptionText(mDescriptionText == null ? DEFAULT_DESCRIPTION_TEXT : mDescriptionText);
            }
        }
    }

    public void setActionText(String actionText) {
        this.mActionText = actionText;
        this.mActionStringView.setText(actionText);
        invalidate();
        requestLayout();
    }

    public void setDescriptionText(String descriptionText) {
        this.mDescriptionText = descriptionText;
        this.mDescriptionStringView.setText(mDescriptionText);
        invalidate();
        requestLayout();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        mActionButton.setOnClickListener(l);
        mActionStringView.setOnClickListener(l);
        mDescriptionStringView.setOnClickListener(l);
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
    public void sendReply(Activity requestFrom, String contentType, Uri contentUri) {
        ShareToMessengerParams shareParams =
                ShareToMessengerParams.newBuilder(contentUri, contentType)
                        .build();

        MessengerUtils.finishShareToMessenger(requestFrom, shareParams);
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
     *                    in the requesting activity's Activity#onActivityResult method
     */
    public void sendMessage(Activity requestFrom, String contentType,
                            Uri contentUri, int requestCode) {
        ShareToMessengerParams shareParams =
                ShareToMessengerParams.newBuilder(contentUri, contentType)
                        .build();

        MessengerUtils.shareToMessenger(
                requestFrom,
                requestCode,
                shareParams);
    }

}