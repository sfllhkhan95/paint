package sfllhkhan95.doodle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import us.feras.mdv.MarkdownView

/**
 * Frequently-Asked Questions (FAQs) about the Doodle application are read in from an markdown
 * file and displayed in this activity.
 *
 * @author saifkhichi96
 * @version 1.0.1
 * @since 3.2.0 25/04/2018 10:24 AM
 */
class FAQsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqs)

        // Set up action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load and display FAQs document
        val markdownView = findViewById<MarkdownView>(R.id.markdownView)
        markdownView.loadMarkdownFile(
                resources.getString(R.string.url_faqs),
                resources.getString(R.string.url_faqs_css)
        )
    }

}