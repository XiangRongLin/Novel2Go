package com.kaiserpudding.novel2go

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.kaiserpudding.novel2go.downloader.CruxDownloader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val view =  super.onCreateView(name, context, attrs)

        val button = view?.findViewById<Button>(R.id.start)
        button?.setOnClickListener {
            val url = view.findViewById<EditText>(R.id.edit_text_url).text.toString()
            val email = view.findViewById<EditText>(R.id.edit_text_email).text.toString()

            val downloader = CruxDownloader()
            downloader.download(url)
        }
        return view
    }

}
