package com.kaiserpudding.novel2go.extractor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.io.File
import java.lang.StringBuilder

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
            val tmpDebug = getString(text)
            paragraphs.add(tmpDebug.replace(regex, ""))
        }
        return paragraphs
    }

    private fun getString(node: Node): String {
        //TextNode if a LeafNode, so no children
        return if (node is TextNode) {
            node.text()
        } else {
            val sb = StringBuilder()
            node.childNodes().forEach {
                sb.append(getString(it))
            }
            sb.toString()
        }
    }


    companion object {
        private const val htmlTagRegex = "<p>|</p>|<em>|</em>"
    }

}
