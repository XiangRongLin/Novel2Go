package com.kaiserpudding.novel2go.extractor

import android.util.Log
import com.kaiserpudding.novel2go.BuildConfig.DEBUG
import java.io.File

class Extractor {

    suspend fun extractSingle(url: String, file: File): String {
        if (DEBUG) Log.d(LOG_TAG, "extractSingle() called with $url")
        val downloader = CruxDownloader()
        val article = downloader.download(url)
        if (!file.exists()) file.mkdirs()
        val pdfCreator = PdfCreator()
        val fileName = article.title.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        pdfCreator.createPdf(article.document, file, fileName)
        return fileName
    }

    companion object {
        private const val LOG_TAG = "Extractor"
    }
}
