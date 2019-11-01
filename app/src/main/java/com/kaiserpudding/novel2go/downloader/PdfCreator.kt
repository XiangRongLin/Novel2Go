package com.kaiserpudding.novel2go.downloader

//import de.wirecard.pdfbox.layout.elements.Document
//import de.wirecard.pdfbox.layout.elements.Paragraph
//import org.apache.pdfbox.pdmodel.font.PDType1Font
//import org.jsoup.nodes.Node
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.text.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.withTranslation
import org.jsoup.nodes.Node
import java.io.File
import java.io.FileOutputStream
import org.jsoup.nodes.Document as JsoupDocument

class PdfCreator {

    companion object {
        fun createPdf(jsoupDocument: JsoupDocument, filesDir: File) {
            val document = PdfDocument()
            val width = 210
            val pageInfo = PdfDocument.PageInfo.Builder( width, 800, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = TextPaint()
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.textSize = 5f
            paint.isLinearText = true

            val paragraphs = parseParagraphs(jsoupDocument.childNodes())

            val alignmen = Layout.Alignment.ALIGN_NORMAL

            var height = 0
            for (paragraph : String in paragraphs) {
                height += canvas.drawMultilineText(paragraph, paint, width - 20, 10f, 20f + height) + 5
//                canvas.drawText(paragraph, 20f, 20f + 5f * multiplier, paint)
            }
            document.finishPage(page)

//            val path = filesDir
//            val file = File(path)
//            if (!file.exists()) {
//                file.mkdirs()
//            }
//            val filePath = File("$path/myfile.pdf")
            Log.d("write_file", "written to $filesDir")
            document.writeTo(FileOutputStream("$filesDir/formatted.pdf"))
            document.close()
        }

        const val htmlTagRegex = "<p>|</p>|<em>|</em>"

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
//    companion object {
//        /**
//         * Creates a PDF document from the given jsoup document.
//         *
//         * @param jsoupDocument
//         */
//        fun createPdf(jsoupDocument: JsoupDocument) {
//            val document = Document(40f, 50f, 40f, 60f)
//            val paragraphs = parseParagraphs(jsoupDocument.childNodes())
//            for (text in paragraphs) {
//                val paragraph = Paragraph()
//                paragraph.addText(text, 12f, PDType1Font.TIMES_ROMAN)
//                document.add(paragraph)
//            }
//            val outputStream = FileOutputStream("generated/file.pdf")
//            document.save(outputStream)
//        }


//    }
}
