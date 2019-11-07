package com.kaiserpudding.novel2go.extractor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import java.io.File

class PdfCreator {

    fun createPdf(jsoupDocument: Document, filesDir: File, fileName: String) {
        val paragraphs = parseParagraphs(jsoupDocument.childNodes())
        val formattedDocument = FormattedDocument()
        for (paragraph: String in paragraphs) {
            formattedDocument.writeText(paragraph)
        }
        formattedDocument.save(filesDir, fileName)
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


    companion object {
        private const val htmlTagRegex = "<p>|</p>|<em>|</em>"
    }

}
