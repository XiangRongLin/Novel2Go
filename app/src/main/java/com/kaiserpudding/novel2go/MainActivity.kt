package com.kaiserpudding.novel2go

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.kaiserpudding.novel2go.extractor.Extractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button_start)
        button.setOnClickListener {
            GlobalScope.launch {
                val url = findViewById<EditText>(R.id.edit_text_url).text.toString()
                val email = findViewById<EditText>(R.id.edit_text_email).text.toString()
                val file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val extractor = Extractor()
                val fileName = extractor.extractSingle(url, file!!)
                startEmailIntent(email, file, fileName)
            }
        }
    }

    private fun startEmailIntent(email: String, file: File, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "convert")
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".fileprovider",
            File(file, fileName)
        )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "message/rfc/822"
        startActivity(Intent.createChooser(intent, "send mail"))
    }
}
