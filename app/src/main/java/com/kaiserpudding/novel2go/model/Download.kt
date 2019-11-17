package com.kaiserpudding.novel2go.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class Download(
    val title: String,
    val path: String,
    val url: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}