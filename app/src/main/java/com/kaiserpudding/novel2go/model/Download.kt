package com.kaiserpudding.novel2go.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Download(
    val title: String,
    val path: String,
    val url: String
) {

    @PrimaryKey
    var id: Long = 0
}