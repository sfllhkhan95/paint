package sfllhkhan95.doodle.projects;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.IOException;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.core.SettingsActivity;
import sfllhkhan95.doodle.core.views.ConfirmationDialog;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 16/06/2018 12:10 AM
 */
public class HomeActivity extends AppCompatActivity implements SpeedDialView.OnActionSelectedListener {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final int REQUEST_PICK_PHOTO = 200;

    private ThumbnailInflater thumbnailInflater;

    private String mCameraPicturePath;

    private AdManager mAdManager;
    private View settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        thumbnailInflater = new ThumbnailInflater(this);

        // Build floating actions
        SpeedDialView composeButton = findViewById(R.id.compose_button);
        composeButton.setOnActionSelectedListener(this);

        composeButton.addActionItem(new SpeedDialActionItem.Builder(R.id.link_camera, R.drawable.ic_open_camera)
                .setLabel(getString(R.string.label_camera))
                .setFabBackgroundColor(getResources().getColor(R.color.red_900))
                .setLabelColor(getResources().getColor(R.color.red_900))
                .create()
        );
        composeButton.addActionItem(new SpeedDialActionItem.Builder(R.id.link_gallery, R.drawable.ic_open_gallery)
                .setLabel(getString(R.string.label_gallery))
                .setFabBackgroundColor(getResources().getColor(R.color.red_900))
                .setLabelColor(getResources().getColor(R.color.red_900))
                .create()
        );
        composeButton.addActionItem(new SpeedDialActionItem.Builder(R.id.link_blank, R.drawable.ic_open_blank)
                .setLabel(getString(R.string.label_blank))
                .setFabBackgroundColor(getResources().getColor(R.color.blue_grey_500))
                .setLabelColor(getResources().getColor(R.color.blue_grey_500))
                .create()
        );

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });
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
        thumbnailInflater.setSavedProjects(DoodleDatabase.INSTANCE.listDoodles());
        runOnUiThread(thumbnailInflater);
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
                .setNegativeButton(getString(android.R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /* no-op */
                    }
                }, true);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
        switch (speedDialActionItem.getId()) {
            case R.id.link_blank:
                Intent blankProjectIntent = new Intent(this, MainActivity.class);
                blankProjectIntent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(blankProjectIntent);
                return true;
            case R.id.link_gallery:
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO);
                    } catch (ActivityNotFoundException ex) {
                        Snackbar.make(settingsButton, "No Gallery application found", Snackbar.LENGTH_LONG).show();
                    }
                }
                return true;
            case R.id.link_camera:
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
                            Snackbar.make(settingsButton, "No Camera application found",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(settingsButton, "Permission to perform storage operations required",
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
                return true;
        }

        return false;
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

}