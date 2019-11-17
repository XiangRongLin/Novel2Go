package com.kaiserpudding.novel2go.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kaiserpudding.novel2go.model.Download

@Dao
interface DownloadDao : BaseDao<Download> {

    @Query("SELECT * FROM downloads" )
    fun getAll() : LiveData<List<Download>>
}