package downloader

import com.chimbori.crux.articles.Article
import com.chimbori.crux.articles.ArticleExtractor
import com.chimbori.crux.urls.CruxURL
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class CruxDownloader {
    private var html : String = ""

    fun download(url: String) : Article {
        downloadHtml(url)

        return ArticleExtractor.with(url, html)
            .extractMetadata()
            .extractContent()
            .estimateReadingTime()
            .article()
    }

    /**
     *
     *
     */
    @Throws(MalformedURLException::class, IOException::class)
    private fun downloadHtml(url: String) {
        val cruxUrl : CruxURL = CruxURL.parse(url) ?: throw MalformedURLException()
        cruxUrl.resolveRedirects()
        if (cruxUrl.isLikelyArticle) {
            val connection = URL(url).openConnection()
            //TODO figure out proper user agent
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/69.0")
            connection.connect()

            val reader = BufferedReader(
                InputStreamReader(connection.getInputStream(), "UTF-8")
            )

            this.html = reader.readText()
            reader.close()
        }
    }
}