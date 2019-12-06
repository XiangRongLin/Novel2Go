package com.kaiserpudding.novel2go.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kaiserpudding.novel2go.model.Download

@Dao
interface DownloadDao : BaseDao<Download> {

    @Query("SELECT * FROM downloads ORDER BY timestamp DESC" )
    fun getAll() : LiveData<List<Download>>

    @Query("DELETE FROM downloads WHERE id IN (:downloads)")
    fun delete(downloads: LongArray)
}