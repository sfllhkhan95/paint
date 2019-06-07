package sfllhkhan95.doodle.views.activity

import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.LinearLayout
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.views.dialog.ConfirmationDialog
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Frequently-Asked Questions (FAQs) about the Doodle application are read in from an markdown
 * file and displayed in this activity.
 *
 * @author saifkhichi96
 * @version 1.0.1
 * @since 3.2.0 25/04/2018 10:24 AM
 */
class FAQsActivity : AppCompatActivity() {

    class FAQ(var question: String, var answer: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqs)

        // Set up action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Read FAQs from file
        val buf = StringBuilder()
        val markdown = assets.open("FAQ.md")
        val `in` = BufferedReader(InputStreamReader(markdown, "UTF-8"))
        var faqs: String? = `in`.readLine()

        while (faqs != null) {
            buf.append(faqs)
            faqs = `in`.readLine()
        }

        `in`.close()

        // Parse FAQs
        val faqList = mutableListOf<FAQ>()
        val questions = buf.split("## ")
        for (q in questions) {
            val content = q.split("?")
            if (content.size < 2) continue
            faqList.add(FAQ(content[0], content[1]))
        }

        // Display FAQs
        val listView = findViewById<LinearLayout>(R.id.faqsView)
        for (f in faqList) {
            layoutInflater.inflate(R.layout.view_settings_item, listView)

            val b = listView.getChildAt(listView.childCount - 1) as MaterialButton
            b.text = "${f.question}?"

            b.setOnClickListener {
                ConfirmationDialog.Builder(this)
                        .setIcon(R.drawable.ic_action_info)
                        .setTitle("${f.question}?")
                        .setMessage(f.answer)
                        .create()
                        .show()
            }
        }
    }

}