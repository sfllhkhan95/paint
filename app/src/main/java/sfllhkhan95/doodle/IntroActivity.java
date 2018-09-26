package sfllhkhan95.doodle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import pk.aspirasoft.core.db.PersistentStorage;
import sfllhkhan95.doodle.core.utils.ThemeAttrs;
import sfllhkhan95.doodle.projects.HomeActivity;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 06/08/2018 2:38 PM
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new SlideFragmentBuilder()
                .image(R.drawable.paintbrush)
                .backgroundColor(ThemeAttrs.INSTANCE.colorPrimaryDark(this))
                .title(getString(R.string.slide1_title))
                .description(getString(R.string.slide1_description))
                .build());

        addSlide(new SlideFragmentBuilder()
                .image(R.drawable.palette)
                .backgroundColor(getResources().getColor(R.color.charcoal))
                .title(getString(R.string.slide2_title))
                .description(getString(R.string.slide2_description))
                .build());

        addSlide(new SlideFragmentBuilder()
                .image(R.drawable.social)
                .backgroundColor(getResources().getColor(R.color.com_facebook_messenger_blue))
                .title(getString(R.string.slide3_title))
                .description(getString(R.string.slide3_description))
                .build());

        boolean storagePermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        boolean cameraPermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        addSlide(new SlideFragmentBuilder()
                .image(R.drawable.permissions)
                .backgroundColor(getResources().getColor(R.color.blood))
                .title(getString(R.string.slide4_title))
                .description(!storagePermissionGranted || !cameraPermissionGranted
                        ? getString(R.string.slide4_description_permissions)
                        : getString(R.string.slide3_description_normal))
                .build());

        askForPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 4);

        showPagerIndicator(false);
        showStatusBar(false);
        showSkipButton(false);
        setBackButtonVisibilityWithDone(false);
        setDoneText(!storagePermissionGranted || !cameraPermissionGranted
                ? getString(R.string.label_intro_done_permission)
                : getString(R.string.label_intro_end_normal));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                } else {
                    Toast.makeText(this, "Grant all permissions before continuing!", Toast.LENGTH_SHORT).show();
                    recreate();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        PersistentStorage.put("INTRO_SEEN", true);

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private class SlideFragmentBuilder {
        private SliderPage fragment;

        private SlideFragmentBuilder() {
            fragment = new SliderPage();
        }

        public SlideFragmentBuilder image(@DrawableRes int imageId) {
            fragment.setImageDrawable(imageId);
            return this;
        }

        public SlideFragmentBuilder title(String title) {
            fragment.setTitle(title);
            return this;
        }

        public SlideFragmentBuilder description(String description) {
            fragment.setDescription(description);
            return this;
        }

        public SlideFragmentBuilder backgroundColor(int color) {
            fragment.setBgColor(color);
            return this;
        }

        public AppIntroFragment build() {
            return AppIntroFragment.newInstance(fragment);
        }
    }

}