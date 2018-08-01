package sfllhkhan95.doodle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import us.feras.mdv.MarkdownView;

/**
 * Frequently-Asked Questions (FAQs) about the Doodle application are read in from an markdown
 * file and displayed in this activity.
 *
 * @author saifkhichi96
 * @version 1.0.1
 * @since 3.2.0 25/04/2018 10:24 AM
 */
public class FAQsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Doodle FAQs");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load and display FAQs document
        MarkdownView markdownView = findViewById(R.id.markdownView);
        markdownView.loadMarkdownFile(
                getResources().getString(R.string.url_faqs),
                getResources().getString(R.string.url_faqs_css)
        );
    }

}