package com.kaiserpudding.novel2go.extractor

import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.graphics.withTranslation
import com.kaiserpudding.novel2go.util.draw
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
        var staticLayout: StaticLayout
        for (paragraph: String in paragraphs) {
            staticLayout = createStaticLayout(
                paragraph,
                paint,
                a4Width - 20
            )
            textHeight = staticLayout.height
            if (sumHeight + textHeight >= a4Height) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(a4Width, a4Height, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                sumHeight = 0

            }
            canvas.withTranslation(10f, 0f + sumHeight) {
                staticLayout.draw(canvas)
            }
            staticLayout.draw(canvas, 10f, 0f + sumHeight)
            sumHeight += textHeight
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

    private fun createStaticLayout(
        text: CharSequence,
        textPaint: TextPaint,
        width: Int,
        start: Int = 0,
        end: Int = text.length,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        spacingMult: Float = 1f,
        spacingAdd: Float = 0f,
        includePad: Boolean = true,
        ellipsizedWidth: Int = width,
        ellipsize: TextUtils.TruncateAt? = null
    ): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, start, end, textPaint, width)
                .setAlignment(alignment)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(includePad)
                .setEllipsizedWidth(ellipsizedWidth)
                .setEllipsize(ellipsize)
                .build()
        } else {
            StaticLayout(
                text, start, end, textPaint, width, alignment,
                spacingMult, spacingAdd, includePad, ellipsize, ellipsizedWidth
            )
        }
    }

}
