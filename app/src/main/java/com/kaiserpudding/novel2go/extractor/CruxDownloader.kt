package com.kaiserpudding.novel2go.extractor

import com.chimbori.crux.articles.Article
import com.chimbori.crux.articles.ArticleExtractor
import com.chimbori.crux.urls.CruxURL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

/**
 * Wrapper class for Crux, which handles fetching the webpage
 *
 */
class CruxDownloader {
    private var html: String = ""

    /**
     * Downloads the html of the website with given url and extract the content with crux.
     *
     * @param url
     * @return The extracted article.
     * @throws MalformedURLException
     */
    @Throws(MalformedURLException::class, IOException::class)
    suspend fun download(url: String): Article {
        downloadHtml(url)
        return ArticleExtractor.with(url, html)
            .extractMetadata()
            .extractContent()
            .estimateReadingTime()
            .article()
    }

    /**
     * Fetches the HTML of the website with given url.
     * Saves it into the html variable
     * @see html
     *
     */
    @Throws(MalformedURLException::class, IOException::class)
    private suspend fun downloadHtml(url: String) {
        withContext(Dispatchers.IO) {
            val cruxUrl: CruxURL = CruxURL.parse(url) ?: throw MalformedURLException()
            cruxUrl.resolveRedirects()
            if (cruxUrl.isLikelyArticle) {
                val connection = URL(url).openConnection()
                //TODO figure out proper user agent
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/69.0"
                )
                connection.connect()

                val reader = BufferedReader(
                    InputStreamReader(connection.getInputStream(), "UTF-8")
                )

                html = reader.readText()
                reader.close()
            }
        }
    }
}