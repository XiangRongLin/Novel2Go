package com.kaiserpudding.novel2go.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.kaiserpudding.novel2go.model.DownloadInfo

@Dao
interface DownloadInfoDao : BaseDao<DownloadInfo> {

    @Insert
    suspend fun insert(downloadInfos: Collection<DownloadInfo>)
}
