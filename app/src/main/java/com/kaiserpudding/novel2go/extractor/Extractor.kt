package com.kaiserpudding.novel2go.extractor

import java.io.File

class Extractor {

    suspend fun extractSingle(url: String, file: File): String {
        val downloader = CruxDownloader()
        val article = downloader.download(url)
        if (!file.exists()) file.mkdirs()
        val pdfCreator = PdfCreator()
        val fileName = article.title.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        pdfCreator.createPdf(article.document, file, fileName)
        return fileName
    }
}
