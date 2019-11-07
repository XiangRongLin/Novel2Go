package com.kaiserpudding.novel2go.extractor

import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import com.kaiserpudding.novel2go.util.drawMultilineText
import com.kaiserpudding.novel2go.util.getTextHeight
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import java.io.File
import java.io.FileOutputStream

class PdfCreator {

    val document = PdfDocument()

    private val a4Width = 595
    private val a4Height = 842
    private val htmlTagRegex = "<p>|</p>|<em>|</em>"

    fun createPdf(jsoupDocument: Document, filesDir: File, fileName: String) {
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(a4Width, a4Height, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.textSize = 13f
        paint.isLinearText = true

        val paragraphs = parseParagraphs(jsoupDocument.childNodes())
        var sumHeight = 0
        var textHeight: Int
        for (paragraph: String in paragraphs) {
            textHeight = canvas.getTextHeight(
                paragraph,
                paint,
                a4Width - 20,
                10f,
                0f + sumHeight
            )
            if (sumHeight + textHeight >= a4Height) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(a4Width, a4Height, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                sumHeight = 0

            }
            sumHeight += canvas.drawMultilineText(
                paragraph,
                paint,
                a4Width - 20,
                10f,
                0f + sumHeight
            )
        }
        document.finishPage(page)
        document.writeTo(FileOutputStream("$filesDir/$fileName"))
        document.close()
    }

    /**
     * Converts the list of nodes to a list of strings with the content of the nodes
     * @see htmlTagRegex
     * @param nodes
     * @return Return List of content of the Nodes
     */
    private fun parseParagraphs(nodes: List<Node>): List<String> {
        val paragraphs = mutableListOf<String>()
        val regex = Regex(htmlTagRegex)
        for (text in nodes) {
            paragraphs.add(text.toString().replace(regex, ""))
        }
        return paragraphs
    }

}
