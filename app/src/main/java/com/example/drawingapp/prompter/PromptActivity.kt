package com.example.drawingapp.prompter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import net.sf.extjwnl.dictionary.Dictionary
import com.example.drawingapp.R
import android.widget.Button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromptActivity : AppCompatActivity() {

    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt)

        val textView: TextView = findViewById(R.id.promptTextView)
        textView.text = "Loading your drawing prompt..."
        textView.textSize = 24f
        textView.setPadding(32, 32, 32, 32)


        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish() // closes this activity and goes back to previous screen
        }

        // Launch coroutine to fetch prompt in background
        mainScope.launch {
            try {
                val wordFetcher = WordFetch(WordNetProvider.dictionary)
                val prompt = wordFetcher.getDrawingPrompt()
                withContext(Dispatchers.Main) {
                    textView.text = prompt
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textView.text = "Did not load prompt: choi"
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
