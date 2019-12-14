package com.kaiserpudding.novel2go.extractor

import android.util.Log
import com.kaiserpudding.novel2go.BuildConfig.DEBUG
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.util.*

class Extractor {

    private val downloader by lazy { CruxDownloader() }
    private val pdfCreator by lazy { PdfCreator() }

    suspend fun extractSingle(url: String, file: File): String {
        if (DEBUG) Log.d(LOG_TAG, "extractSingle() called with $url")
        val article = downloader.download(url)
        if (!file.exists()) file.mkdirs()
        val fileName = article.title.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        pdfCreator.createPdf(article.document, file, fileName)
        return fileName
    }

    suspend fun extractMulti(
        link: String,
        file: File,
        regex: Regex = "chapter \\d+".toRegex()
    ): List<String> {
        if (DEBUG) Log.d(LOG_TAG, "extractMulti() called with $link")
        val fileNames = LinkedList<String>()
        getUrls(link, regex).forEach {
            fileNames.add(extractSingle(it, file))
            delay(5000)
        }
        return fileNames
    }

    private fun getUrls(link: String, regex: Regex): List<String> {
        val url = URL(link)
        val doc = Jsoup.connect(link).userAgent(USER_AGENT).get()
        val urls = LinkedList<String>()
        doc.select("a").filter {
            val href = it.attr(HTML_HREF_ATTR)
//        val found = "\\d+\\.".toRegex().find(it.text())
//        if (found != null) {
//            found.range.first == 0 && a != "/" && (a.contains(url.host, true) || (a.startsWith(
//                "/"
//            ) && !a.startsWith("//")))
            val filter = it.hasText()
                    && it.text().toLowerCase(Locale.ENGLISH).contains(regex)
                    && href != "/"
                    && (href.contains(url.host, true)
                    || (href.startsWith("/")
                    && !href.startsWith("//")))
            if (DEBUG) Log.v(LOG_TAG, "getUrls() regex: $regex, url: $href, isChapter: $filter")
            filter
//        } else {
//            false
//        }

        }.forEach {
            var href = it.attr(HTML_HREF_ATTR)
            if (href.startsWith("/")) href = url.protocol + "://" + url.host + href
            urls.add(href)
        }
        return urls
    }


    companion object {
        private const val LOG_TAG = "Extractor"
        private const val HTML_LINK_TAG = "a"
        private const val HTML_HREF_ATTR = "href"
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0"
    }
}
