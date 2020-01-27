package com.kaiserpudding.novel2go.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

/**
 * Information about the downloaded chapter for local use.
 *
 * @property title
 * @property path
 * @property url
 */
@Entity(tableName = "downloads")
data class Download(
    val title: String,
    val path: String,
    val url: String,
    var timestamp: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * A file with the path and filename
     */
    val file: File
        get() = File(path, "$title.pdf")
}