package sfllhkhan95.doodle.core;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.MessengerUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.views.MessengerShareButton;
import sfllhkhan95.doodle.core.models.PaintCanvas;
import sfllhkhan95.doodle.core.models.tools.Circle;
import sfllhkhan95.doodle.core.models.tools.Eraser;
import sfllhkhan95.doodle.core.models.tools.Line;
import sfllhkhan95.doodle.core.models.tools.Pen;
import sfllhkhan95.doodle.core.models.tools.Quad2D;
import sfllhkhan95.doodle.core.models.tools.Quad3D;
import sfllhkhan95.doodle.core.utils.ActionBarManager;
import sfllhkhan95.doodle.core.utils.DialogFactory;
import sfllhkhan95.doodle.core.utils.OnColorPickedListener;
import sfllhkhan95.doodle.core.utils.OnToolSelectedListener;
import sfllhkhan95.doodle.core.views.CanvasColorPicker;
import sfllhkhan95.doodle.core.views.ColorPicker;
import sfllhkhan95.doodle.core.views.PaintView;
import sfllhkhan95.doodle.core.views.ToolboxView;
import sfllhkhan95.doodle.projects.utils.DoodleFactory;

/**
 * @version 2.0.0
 * @since 1.0.0
 */
public class MainActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnToolSelectedListener,
        OnColorPickedListener, View.OnTouchListener {

    private static final int REQUEST_CODE_SHARE = 100;
    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 200;

    // Brush controller
    private SeekBar brushController;

    // A custom OpenGL ES canvas to draw on
    private PaintView paintView;

    // Toolbox contains the drawing tools
    private ToolboxView toolbox;

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private DialogFactory dialogFactory;

    // Action bars
    private CustomToolbar toolbar;
    private boolean isMaximized;

    // Are we in a REPLY flow?
    private boolean mReplying;
    private MessengerShareButton messengerShareButton;

    // Firebase Analytics (For event logging)
    private FirebaseAnalytics mFirebaseAnalytics;

    // Is this a new or existing project?
    private boolean isExisting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        this.toolbar = new CustomToolbar();

        this.brushController = findViewById(R.id.brushController);
        this.brushController.setOnSeekBarChangeListener(this);

        // Obtain the FirebaseAnalytics instance.
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain device display metrics (used to setup project resolution)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Initialize canvas where everything is drawn
        paintView = findViewById(R.id.canvas);
        paintView.setOnTouchListener(this);
        Intent intent = getIntent();
        boolean messengerAction = intent.getBooleanExtra("MESSENGER", false);
        messengerShareButton = findViewById(R.id.messenger_share_button);
        if (Intent.ACTION_PICK.equals(intent.getAction()) || messengerAction) {
            messengerShareButton.setVisibility(View.VISIBLE);
            messengerShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShareClicked(true);
                }
            });
            if (Intent.ACTION_PICK.equals(intent.getAction())) {
                Bundle logParams = new Bundle();
                mFirebaseAnalytics.logEvent("reply_messenger", logParams);

                mReplying = true;
                messengerShareButton.setActionText("Replying to");
                messengerShareButton.setDescriptionText("Messenger Conversation");

                MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);
                List<String> participantIds = mThreadParams.participants;
                if (participantIds != null && participantIds.size() > 0) {
                    String replyingTo = participantIds.get(0);
                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/" + replyingTo,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    String recipientName;
                                    try {
                                        recipientName = response.getJSONObject().get("name").toString();
                                        messengerShareButton.setDescriptionText(recipientName);
                                    } catch (NullPointerException | JSONException ignored) {

                                    }
                                }
                            });
                    request.executeAsync();
                }
            }

            initCanvas(startFromScratch(metrics));
        } else {
            initCanvas(getPaintCanvas(metrics));
        }

        // Create confirmation dialogs
        dialogFactory = new DialogFactory(this, paintView);

        // Add click event listeners to toolbox buttons
        toolbox = findViewById(R.id.toolbox);
        toolbox.updatePenColorPicker(paintView.getBrush().getStrokeColor());

        // Start in windowed mode
        this.isMaximized = false;
        onMaximizeToggled(false);

        // Select pen
        findViewById(R.id.pen).callOnClick();
    }

    private void initCanvas(PaintCanvas canvas) {
        if (paintView != null) {
            paintView.setCanvas(canvas);
            paintView.setShapeType(Pen.class);  // Select Pen by default
            paintView.getBrush().setSize(brushController.getProgress());
        }
    }

    private PaintCanvas getPaintCanvas(DisplayMetrics metrics) {
        PaintCanvas canvas;
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        String cameraImage = getIntent().getStringExtra("FROM_CAMERA");
        Intent galleryImage = getIntent().getParcelableExtra("FROM_GALLERY");
        if (savedDoodle != null && !savedDoodle.isEmpty()) {
            canvas = resumeProject(metrics, savedDoodle);
        } else if (galleryImage != null) {
            canvas = startFromGallery(metrics, galleryImage);
        } else if (cameraImage != null) {
            canvas = startFromCamera(metrics, cameraImage);
        } else {
            canvas = startFromScratch(metrics);
        }
        return canvas;
    }

    private PaintCanvas startFromGallery(DisplayMetrics metrics, Intent galleryImage) {
        Bundle logParams = new Bundle();
        boolean success = false;

        PaintCanvas canvas;
        try {
            Uri selectedImage = galleryImage.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmapFromFile = DoodleFactory.loadFromPath(picturePath, metrics.widthPixels, metrics.heightPixels);
            canvas = PaintCanvas.loadFromBitmap(this, metrics, bitmapFromFile);

            success = true;
        } catch (Exception ex) {
            canvas = startFromScratch(metrics);
        } finally { // Log event
            logParams.putBoolean("success", success);
            mFirebaseAnalytics.logEvent("from_gallery", logParams);
        }
        return canvas;
    }

    private PaintCanvas startFromCamera(DisplayMetrics metrics, String cameraImage) {
        Bundle logParams = new Bundle();
        boolean success = false;

        PaintCanvas canvas;
        try {
            Bitmap bitmapFromFile = DoodleFactory.loadFromPath(cameraImage, metrics.widthPixels, metrics.heightPixels);
            canvas = PaintCanvas.loadFromBitmap(this, metrics, bitmapFromFile);

            success = true;
        } catch (Exception ex) {
            canvas = startFromScratch(metrics);
        } finally { // Log event
            logParams.putBoolean("success", success);
            mFirebaseAnalytics.logEvent("from_camera", logParams);
        }
        return canvas;
    }

    private PaintCanvas startFromScratch(DisplayMetrics metrics) {
        PaintCanvas canvas = new PaintCanvas(this, metrics);

        // Log event
        Bundle logParams = new Bundle();
        logParams.putBoolean("success", true);
        mFirebaseAnalytics.logEvent("from_scratch", logParams);

        return canvas;
    }

    private PaintCanvas resumeProject(DisplayMetrics metrics, String savedDoodle) {
        isExisting = true;
        return PaintCanvas.loadFromPath(this, metrics, savedDoodle);
    }

    private void share(File tempFile) {
        // Get a shareable file URI
        String contentType = "image/jpeg";
        Uri contentUri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider",
                tempFile);

        // Start share sequence
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(contentType);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivityForResult(Intent.createChooser(share, "Share Doodle"), REQUEST_CODE_SHARE);
    }

    private void shareOnMessenger(File tempFile) {
        // Get a shareable file URI
        String contentType = "image/jpeg";
        Uri contentUri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider",
                tempFile
        );

        // Start share sequence
        if (mReplying) {
            messengerShareButton.sendReply(this, contentType, contentUri);
        } else {
            messengerShareButton.sendMessage(this, contentType, contentUri, REQUEST_CODE_SHARE_TO_MESSENGER);
        }
    }

    /**
     * @param isMaximized
     * @since 3.4.3
     */
    private void setMaximized(boolean isMaximized) {
        this.isMaximized = isMaximized;
        onMaximizeToggled(this.isMaximized);
    }

    private void onMaximizeToggled(boolean isMaximized) {
        findViewById(R.id.toolbox).setVisibility(isMaximized ? View.GONE : View.VISIBLE);
        findViewById(R.id.brushSizeBar).setVisibility(isMaximized ? View.GONE : View.VISIBLE);
        toolbar.configure(isMaximized);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(brushController)) {
            paintView.getBrush().setSize(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isMaximized) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);

            if (mReplying) {
                MenuItem item = menu.findItem(R.id.share);
                item.setVisible(false);
            }

            if (isExisting) {
                MenuItem item = menu.findItem(R.id.canvas);
                item.setVisible(false);
            }

            ActionBarManager mActionBarManager = new ActionBarManager(menu);
            paintView.setCanvasActionListener(mActionBarManager);
            mActionBarManager.sync(paintView);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.canvas:
                CanvasColorPicker canvasColorPicker = new CanvasColorPicker(this, paintView.getCanvas().getColor());
                canvasColorPicker.setOnColorPickedListener(new OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color) {
                        paintView.getCanvas().setColor(color);
                        MainActivity.this.onColorPicked(~color | 0xFF000000);
                        paintView.invalidate();
                    }
                });
                canvasColorPicker.show();
                return true;

            case R.id.undo:
                paintView.undo();
                return true;

            case R.id.redo:
                paintView.redo();
                return true;

            case R.id.revert:
                dialogFactory.revertConfirmationDialog(this).show();
                return true;

            case R.id.save:
                if (paintView.isModified()) {
                    if (isExisting) {
                        dialogFactory.saveAsConfirmationDialog(this).show();
                    } else {
                        dialogFactory.saveConfirmationDialog(this).show();
                    }
                }
                return true;

            case R.id.share:
                dialogFactory.shareDialog(this).show();
                return true;
        }

        return false;
    }

    public void onShareClicked(boolean messengerExpression) {
        if (isExisting || paintView.isModified()) {
            try {
                // Get the drawn bitmap from paint canvas
                PaintCanvas canvas = paintView.getCanvas();
                Bitmap bitmapToShare = canvas.getBitmap();

                // Compress bitmap
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmapToShare.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                // Write to a temporary file
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                String path = storageDir + File.separator + "SHARE_IMAGE.jpg";
                File tempFile = new File(path);
                boolean created = tempFile.createNewFile();
                Log.i(DoodleApplication.TAG, created ? "New temporary file created." : "Temporary file already exists. Overwriting!");

                FileOutputStream fo = new FileOutputStream(tempFile);
                fo.write(bytes.toByteArray());

                if (messengerExpression) {
                    shareOnMessenger(tempFile);
                } else {
                    share(tempFile);
                }
            } catch (Exception e) { // If, for some reason, sharing fails
                // Log exception for error tracking
                Crashlytics.logException(e);

                // Display a nice error message to the user
                Snackbar.make(
                        messengerShareButton,
                        getString(R.string.unknownError),
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                ).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SHARE || requestCode == REQUEST_CODE_SHARE_TO_MESSENGER) {
            String path = Environment.getExternalStorageDirectory() + File.separator + "SHARE_IMAGE.jpg";
            File tempFile = new File(path);
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    Log.e(DoodleApplication.TAG, "Failed to remove temporary file.");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (paintView.isModified()) {
            dialogFactory.exitConfirmationDialog(this).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onToolSelected(boolean reset, int id) {
        if (reset) {
            paintView.setShapeType(null);
        }
        switch (id) {
            case R.id.pen:
                paintView.setShapeType(Pen.class);
                break;

            case R.id.colorPicker:
                paintView.setShapeType(sfllhkhan95.doodle.core.models.tools.ColorPicker.class);
                paintView.setOnColorPickedListener(this);
                break;

            case R.id.line:
                paintView.setShapeType(Line.class);
                break;
            case R.id.rect:
                paintView.setShapeType(Quad2D.class);
                break;
            case R.id.box:
                paintView.setShapeType(Quad3D.class);
                break;
            case R.id.circle:
                paintView.setShapeType(Circle.class);
                break;

            case R.id.penColorPicker:
                ColorPicker strokePicker = new ColorPicker(this, paintView.getBrush().getStrokeColor());
                strokePicker.setOnColorPickedListener(this);
                strokePicker.show();
                break;
            case R.id.fillColorPicker:
                ColorPicker fillPicker = new ColorPicker(this, paintView.getBrush().getFillColor());
                fillPicker.setOnColorPickedListener(new OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color) {
                        paintView.getBrush().setFillColor(color);
                        toolbox.updateFillColorPicker(color);
                    }
                });
                fillPicker.show();
                break;

            case R.id.eraser:
                paintView.setShapeType(Eraser.class);
                break;
        }

        toolbox.updateFillColorPicker(paintView.getBrush().getFillColor());
        toolbox.updatePenColorPicker(paintView.getBrush().getStrokeColor());
    }

    @Override
    public void onColorPicked(int color) {
        paintView.getBrush().setStrokeColor(color);
        toolbox.updatePenColorPicker(color);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!paintView.getShapeType().equals(sfllhkhan95.doodle.core.models.tools.ColorPicker.class)) {
                    setMaximized(false);
                } else {
                    findViewById(R.id.pen).performClick();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                if (!paintView.getShapeType().equals(sfllhkhan95.doodle.core.models.tools.ColorPicker.class)) {
                    setMaximized(true);
                }

                view.performClick();
                break;
        }
        return false;
    }

    private class CustomToolbar {
        private Toolbar primary;

        CustomToolbar() {
            primary = findViewById(R.id.primaryToolbar);
            primary.setOverflowIcon(getResources().getDrawable(R.drawable.ic_action_layers));
            primary.setTitle("");
        }

        void configure(boolean isMaximized) {
            primary.setVisibility(isMaximized ? View.GONE : View.VISIBLE);
            setSupportActionBar(primary);
        }
    }
}