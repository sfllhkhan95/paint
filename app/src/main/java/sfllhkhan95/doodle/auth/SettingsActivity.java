package sfllhkhan95.doodle.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.FAQsActivity;
import sfllhkhan95.doodle.PrivacyPolicy;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.utils.DialogFactory;

import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DEFAULT;
import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_OCEAN;
import static sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_SUNLIGHT;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
public class SettingsActivity extends AppCompatActivity implements
        View.OnClickListener {

    private AppCompatButton themeButton;
    private final CharSequence[] themes = new CharSequence[]{"Default", "Ocean", "Sunlight"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int which = ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Configure action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        themeButton = findViewById(R.id.theme_selected);
        themeButton.setText(themes[which]);
        themeButton.setOnClickListener(this);

        findViewById(R.id.theme).setOnClickListener(this);
        findViewById(R.id.support).setOnClickListener(this);
        findViewById(R.id.privacy_policy).setOnClickListener(this);
        findViewById(R.id.faqs).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.theme_selected:
            case R.id.theme:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme))
                        .setTitle("Choose Theme")
                        .setItems(themes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                themeButton.setText(themes[which]);
                                String theme = THEME_DEFAULT;
                                switch (which) {
                                    case 1:
                                        theme = THEME_OCEAN;
                                        break;
                                    case 2:
                                        theme = THEME_SUNLIGHT;
                                }
                                ((DoodleApplication) getApplication())
                                        .changeTheme(SettingsActivity.this, theme);
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;

            case R.id.privacy_policy:
                startActivity(new Intent(this, PrivacyPolicy.class));
                break;

            case R.id.faqs:
                startActivity(new Intent(this, FAQsActivity.class));
                break;

            case R.id.support:
                new DialogFactory(this, null).
                        supportDialog(this).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

}