package sfllhkhan95.doodle.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import sfllhkhan95.doodle.FAQsActivity;
import sfllhkhan95.doodle.PrivacyPolicy;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.utils.DialogFactory;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
public class SettingsActivity extends AppCompatActivity implements
        View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Configure action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.about_developer).setOnClickListener(this);
        findViewById(R.id.privacy_policy).setOnClickListener(this);
        findViewById(R.id.faqs).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.privacy_policy:
                startActivity(new Intent(this, PrivacyPolicy.class));
                break;

            case R.id.faqs:
                startActivity(new Intent(this, FAQsActivity.class));
                break;

            case R.id.about_developer:
                new DialogFactory(this, null).
                        supportDialog(this).show();
                break;
        }
    }

}