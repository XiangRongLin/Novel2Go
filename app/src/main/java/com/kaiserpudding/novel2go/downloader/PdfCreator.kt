package com.kaiserpudding.novel2go.downloader

import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.jsoup.nodes.Node
import org.jsoup.nodes.Document as JsoupDocument
import rst.pdfbox.layout.elements.Document
import rst.pdfbox.layout.elements.Paragraph
import java.io.FileOutputStream

fun createPdf(jsoupDocument: JsoupDocument) {
    val document = Document(40f, 50f, 40f, 60f)
    val paragraphs = parseParagraphs(jsoupDocument.childNodes())
    for (text in paragraphs) {
        val paragraph = Paragraph()
        paragraph.addText(text, 12f, PDType1Font.TIMES_ROMAN)
        document.add(paragraph)
    }
    val outputStream = FileOutputStream("generated/file.pdf")
    document.save(outputStream)
}

const val htmlTagRegex = "<p>|</p>"

fun parseParagraphs(nodes: List<Node>): List<String> {
    val paragraphs = mutableListOf<String>()
    val regex = Regex(htmlTagRegex)
    for (text in nodes) {
        paragraphs.add(text.toString().replace(regex, ""))
    }
    return paragraphs
}