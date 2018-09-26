package sfllhkhan95.doodle.projects;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.widget.LoginButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;
import sfllhkhan95.doodle.auth.SettingsActivity;
import sfllhkhan95.doodle.auth.models.User;
import sfllhkhan95.doodle.auth.utils.AuthHandler;
import sfllhkhan95.doodle.auth.utils.OnUpdateListener;
import sfllhkhan95.doodle.auth.views.UserView;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.core.utils.ThemeAttrs;
import sfllhkhan95.doodle.core.views.ConfirmationDialog;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 16/06/2018 12:10 AM
 */
public class HomeActivity extends AppCompatActivity implements OnUpdateListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final int REQUEST_PICK_PHOTO = 200;

    private final User DEFAULT_USER = new User();
    private AuthHandler mAuthHandler;
    private UserView mUserView;

    private ThumbnailInflater thumbnailInflater;

    private RapidFloatingActionHelper rfaHelper;
    private String mCameraPicturePath;

    private AdManager mAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        thumbnailInflater = new ThumbnailInflater(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Build floating actions
        RapidFloatingActionButton composeButton = findViewById(R.id.compose_button);
        RapidFloatingActionLayout composeMenu = findViewById(R.id.compose_list);

        RapidFloatingActionContentLabelList composeList = new RapidFloatingActionContentLabelList(this);
        composeList.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_camera))
                .setResId(R.drawable.ic_open_camera)
                .setIconNormalColor(getResources().getColor(R.color.orange))
                .setIconPressedColor(getResources().getColor(R.color.slate_translucent))
                .setLabelColor(getResources().getColor(R.color.orange))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_gallery))
                .setResId(R.drawable.ic_open_gallery)
                .setIconNormalColor(getResources().getColor(R.color.blood))
                .setIconPressedColor(getResources().getColor(R.color.slate_translucent))
                .setLabelColor(getResources().getColor(R.color.blood))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_blank))
                .setResId(R.drawable.ic_open_blank)
                .setIconNormalColor(getResources().getColor(R.color.slate))
                .setIconPressedColor(getResources().getColor(R.color.slate_translucent))
                .setLabelColor(getResources().getColor(R.color.slate))
                .setWrapper(2)
        );

        composeList.setItems(items)
                .setIconShadowColor(R.color.slate_translucent);

        rfaHelper = new RapidFloatingActionHelper(
                this,
                composeMenu,
                composeButton,
                composeList
        ).build();

        findViewById(R.id.facebookConnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuthHandler.isSignedIn()) {
                    ((TextView) findViewById(R.id.facebookConnectionStatus)).setText(R.string.connecting);
                }

                findViewById(R.id.loginButton).performClick();
            }
        });

        // Assign views
        mUserView = new UserView(this)
                .setAvatarView((ImageView) findViewById(R.id.userAvatar));

        // Configure authentication
        mAuthHandler = new AuthHandler(this);
        mAuthHandler.setOnUpdateListener(this);
        if (!mAuthHandler.isSignedIn()) {
            mAuthHandler.signIn();
        }

        // Set up Facebook login button
        LoginButton mLoginButton = findViewById(R.id.loginButton);
        mAuthHandler.registerFacebookLoginButton(mLoginButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdManager = AdManager.Companion.getInstance();
        mAdManager.loadVideoAd(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Inflate thumbnails of saved projects
        thumbnailInflater.setSavedProjects(DoodleDatabase.listDoodles());
        runOnUiThread(thumbnailInflater);

        // Update UI
        onUpdate();
    }

    @Override
    public void onUpdate() {
        if (mAuthHandler.isSignedIn()) {
            mUserView.showUser(mAuthHandler.getCurrentUser());
            findViewById(R.id.facebookConnectButton).setVisibility(View.GONE);
            findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mAuthHandler.getCurrentUser().getFirstName());
            }
        } else {
            mUserView.showUser(DEFAULT_USER);
            ((TextView) findViewById(R.id.facebookConnectionStatus)).setText(R.string.connection_no);
            findViewById(R.id.facebookConnectButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutButton).setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.app_name));
            }
        }
    }

    @Override
    public void onBackPressed() {
        ConfirmationDialog.Builder mBuilder = new ConfirmationDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setHeadline(getString(R.string.label_exit))
                .setTitle(getString(R.string.confirm_quit))
                .setMessage(getString(R.string.description_quit))
                .setPositiveButton(getString(android.R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HomeActivity.super.onBackPressed();
                    }
                }, true)
                .setNegativeButton(getString(android.R.string.cancel), null, true);

        if (mAdManager != null && mAdManager.isVideoAdLoaded()) {
            mBuilder.setMessage(getString(R.string.description_watch_ad))
                    .setNegativeButton(getString(R.string.label_watch_ad), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdManager.showVideoAd(HomeActivity.this);
                        }
                    }, false);
        }

        mBuilder.create().show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthHandler != null) {
            mAuthHandler.stopTracking();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            mAuthHandler.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException ignored) {
            //
        }
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_PICK_PHOTO:
                if (data != null) {
                    Intent openGalleryImage = new Intent(getApplicationContext(), MainActivity.class);
                    openGalleryImage.putExtra("FROM_GALLERY", data);
                    startActivity(openGalleryImage);
                }
                break;
            case REQUEST_TAKE_PHOTO:
                if (mCameraPicturePath != null && !mCameraPicturePath.isEmpty()) {
                    Intent openCameraImage = new Intent(getApplicationContext(), MainActivity.class);
                    openCameraImage.putExtra("FROM_CAMERA", mCameraPicturePath);
                    startActivity(openCameraImage);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        // Try to tint the action bar icon
        try {
            MenuItem menuItem = menu.findItem(R.id.settings);
            Drawable drawable = menuItem.getIcon();
            drawable.mutate();
            drawable.setColorFilter(ThemeAttrs.INSTANCE.colorPrimaryDark(this),
                    PorterDuff.Mode.SRC_IN);
        } catch (NullPointerException ex) {
            Crashlytics.logException(ex);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
        }
        return false;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        switch (item.getResId()) {
            case R.drawable.ic_open_blank:
                Intent blankProjectIntent = new Intent(this, MainActivity.class);
                blankProjectIntent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(blankProjectIntent);
                break;
            case R.drawable.ic_open_gallery:
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO);
                    } catch (ActivityNotFoundException ex) {
                        Snackbar.make(mUserView, "No Gallery application found", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case R.drawable.ic_open_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                        Uri photoURI = FileProvider.getUriForFile(this,
                                getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider",
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        } catch (ActivityNotFoundException ex) {
                            Snackbar.make(mUserView, "No Camera application found",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(mUserView, "Permission to perform storage operations required",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Grant", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(HomeActivity.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    100);
                                        }
                                    }).show();
                        }
                    }
                }
                break;
        }
        rfaHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        onRFACItemLabelClick(position, item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String path = storageDir + File.separator + "CAMERA_IMAGE.jpg";
        File image = new File(path);
        boolean created = image.createNewFile();
        Log.i(DoodleApplication.TAG, created ? "New temporary file created." : "Temporary file already exists. Overwriting!");

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPicturePath = image.getAbsolutePath();
        return image;
    }

    public void showList(View view) {
        ((ImageView) findViewById(R.id.gridButton)).setColorFilter(getResources().getColor(R.color.white));
        ((ImageView) findViewById(R.id.listButton)).setColorFilter(ThemeAttrs.INSTANCE.colorPrimaryDark(this));
        ((ImageView) findViewById(R.id.notificationsButton)).setColorFilter(getResources().getColor(R.color.white));

        findViewById(R.id.savedProjectsGrid).setVisibility(View.GONE);
        findViewById(R.id.savedProjectsList).setVisibility(View.VISIBLE);

        ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
    }

    public void showGrid(View view) {
        ((ImageView) findViewById(R.id.gridButton)).setColorFilter(ThemeAttrs.INSTANCE.colorPrimaryDark(this));
        ((ImageView) findViewById(R.id.listButton)).setColorFilter(getResources().getColor(R.color.white));
        ((ImageView) findViewById(R.id.notificationsButton)).setColorFilter(getResources().getColor(R.color.white));

        findViewById(R.id.savedProjectsGrid).setVisibility(View.VISIBLE);
        findViewById(R.id.savedProjectsList).setVisibility(View.GONE);

        ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
    }

    public void signOut(View view) {
        findViewById(R.id.loginButton).performClick();
    }

}