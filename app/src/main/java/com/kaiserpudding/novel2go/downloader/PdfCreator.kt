package com.kaiserpudding.novel2go.downloader

import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import org.jsoup.nodes.Node
import java.io.File
import java.io.FileOutputStream
import org.jsoup.nodes.Document as JsoupDocument

class PdfCreator {

    companion object {
        fun createPdf(jsoupDocument: JsoupDocument, filesDir: File, fileName: String) {
            val document = PdfDocument()
            val width = 210
            val pageInfo = PdfDocument.PageInfo.Builder(width, 800, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = TextPaint()
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.textSize = 5f
            paint.isLinearText = true

            val paragraphs = parseParagraphs(jsoupDocument.childNodes())

            var height = 0
            for (paragraph: String in paragraphs) {
                height += canvas.drawMultilineText(
                    paragraph,
                    paint,
                    width - 20,
                    10f,
                    20f + height
                ) + 5
            }
            document.finishPage(page)
            document.writeTo(FileOutputStream("$filesDir/$fileName"))
            document.close()
        }

        private const val htmlTagRegex = "<p>|</p>|<em>|</em>"

        /**
         * Converts the list of nodes to a list of strings with the content of the nodes
         * @see htmlTagRegex
         * @param nodes
         * @return Return List of content of the Nodes
         */
        fun parseParagraphs(nodes: List<Node>): List<String> {
            val paragraphs = mutableListOf<String>()
            val regex = Regex(htmlTagRegex)
            for (text in nodes) {
                paragraphs.add(text.toString().replace(regex, ""))
            }
            return paragraphs
        }
    }
}
