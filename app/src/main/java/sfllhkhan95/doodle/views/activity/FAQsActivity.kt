package sfllhkhan95.doodle.views.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
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
        val faqAnswers = mutableListOf<TextView>()
        for (faq in faqList) {
            layoutInflater.inflate(R.layout.view_faq, listView)
            val v = listView.getChildAt(listView.childCount - 1)

            v.findViewById<TextView>(R.id.description)?.let { answerView ->
                answerView.text = faq.answer
                faqAnswers.add(answerView)

                v.findViewById<TextView>(R.id.title)?.let { questionView ->
                    questionView.text = "${faq.question}?"
                    questionView.setOnClickListener {
                        for (answer in faqAnswers) {
                            if (answer != answerView) {
                                answer.visibility = View.GONE
                            }
                        }

                        if (answerView.visibility == View.GONE) {
                            answerView.visibility = View.VISIBLE
                        } else {
                            answerView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}