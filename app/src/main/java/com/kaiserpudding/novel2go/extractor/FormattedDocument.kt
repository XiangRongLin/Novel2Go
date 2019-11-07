package com.kaiserpudding.novel2go.extractor

import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.graphics.withTranslation
import java.io.File
import java.io.FileOutputStream

class FormattedDocument {

    var paint: TextPaint
    var paddingRight = 10
    var paddingLeft = 10

    private val document = PdfDocument()
    private var pageNumber = 1
    private var page: PdfDocument.Page
    private var usedHeight = 0

    init {
        page = document.startPage(
            PdfDocument.PageInfo.Builder(
                a4Width,
                a4Height,
                pageNumber
            ).create()
        )
        paint = TextPaint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.textSize = 13f
        paint.isLinearText = true
    }

    fun writeText(text: String) {
        val staticLayout = createStaticLayout(
            text, paint, a4Width - (paddingLeft + paddingRight)
        )

        if (staticLayout.height >= a4Height - usedHeight) nextPage()

        page.canvas.withTranslation(10f, 0f + usedHeight) {
            staticLayout.draw(page.canvas)
        }
        usedHeight += staticLayout.height
    }

    fun nextPage() {
        document.finishPage(page)
        page = document.startPage(
            PdfDocument.PageInfo.Builder(
                a4Width,
                a4Height,
                pageNumber
            ).create()
        )
        pageNumber++
        usedHeight = 0
    }

    fun save(filesDir: File, fileName: String) {
        document.finishPage(page)
        document.writeTo(FileOutputStream("$filesDir/$fileName.pdf"))
        document.close()
    }

    fun save(outputStream: FileOutputStream) {
        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
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

    companion object {
        private const val a4Width = 595
        private const val a4Height = 842
    }
}