package sfllhkhan95.doodle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

import sfllhkhan95.doodle.projects.HomeActivity;

/**
 * @author saifkhichi96
 * @version 1.2.0
 * @since 1.0.0
 * created on 23/10/2017 2:26 AM
 */
public class LaunchScreen extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST = 200;

    private final long delay = 1500L;
    private final Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST);
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
            }, delay);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                mAuth.signInAnonymously();
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }, delay);

                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            timer.cancel();
        } finally {
            super.onDestroy();
        }
    }
}
