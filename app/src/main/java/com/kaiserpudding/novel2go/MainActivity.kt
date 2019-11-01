package com.kaiserpudding.novel2go

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.kaiserpudding.novel2go.downloader.CruxDownloader
import com.kaiserpudding.novel2go.downloader.PdfCreator
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
                val downloader = CruxDownloader()
                val article = downloader.download(url)
                val file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                file?.mkdirs()
                val fileName = "filenName.pdf"

                if (file != null) {
                    PdfCreator.createPdf(article.document, file, fileName)
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                intent.putExtra(Intent.EXTRA_SUBJECT, "convert")
                val uri = Uri.fromFile(File(file, fileName))
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.setType("message/rfc/822")
                startActivity(Intent.createChooser(intent, "send mail"))
            }
        }
    }
}
