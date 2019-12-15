package com.kaiserpudding.novel2go.extractor

import android.util.Log
import com.kaiserpudding.novel2go.BuildConfig.DEBUG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    /**
     * Extracts multiple file from the given [link] of a "table of contents" site.
     * The chapters get identified by the [regex].
     *
     * @param link The url to extract the links from.
     * @param file The place to save the downloaded files.
     * @param regex Used to identify which urls in the html of the given link to download.
     * Default is "chapter \\d+" ("chapter " followed by numbers).
     * @return A [Channel] of [Pair] of [String].
     * [Pair.first] is the url of the download, [Pair.second] is the file name.
     */
    suspend fun extractMulti(
        link: String,
        file: File,
        regex: Regex = "chapter \\d+".toRegex()
    ): Channel<Pair<String, String>> {
        if (DEBUG) Log.d(LOG_TAG, "extractMulti() called with $link")
        val channel = Channel<Pair<String, String>>()
        GlobalScope.launch {
            getUrls(link, regex).forEach { url ->
                val fileName = extractSingle(url, file)
                if (DEBUG) Log.d(
                    LOG_TAG,
                    "extractMulti() channel sent url: $url, file name: $fileName"
                )
                channel.send(Pair(url, fileName))
                delay(5000)
            }
            if (DEBUG) Log.d(LOG_TAG, "extractMulti() channel closing")
            channel.close()
        }
        return channel
    }

    private fun getUrls(link: String, regex: Regex): List<String> {
        val url = URL(link)
        val doc = Jsoup.connect(link).userAgent(USER_AGENT).get()
        val urls = LinkedList<String>()
        doc.select(HTML_LINK_TAG).filter {
            val href = it.attr(HTML_HREF_ATTR)
            val filter = it.hasText()
                    && it.text().toLowerCase(Locale.ENGLISH).contains(regex)
                    && href != "/"
                    && (href.contains(
                url.host,
                true
            ) || (href.startsWith("/") && !href.startsWith("//")))
            if (DEBUG) Log.v(LOG_TAG, "getUrls() regex: $regex, url: $href, isChapter: $filter")
            filter
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
