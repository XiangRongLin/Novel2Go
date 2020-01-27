package com.kaiserpudding.novel2go.repositories

import com.kaiserpudding.novel2go.database.dao.DownloadInfoDao
import com.kaiserpudding.novel2go.model.DownloadInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadInfoRepository(private val downloadInfoDao: DownloadInfoDao) {

    suspend fun insert(downloadInfo: DownloadInfo): Long {
        return withContext(Dispatchers.IO) {
            return@withContext downloadInfoDao.insert(downloadInfo)
        }
    }

    suspend fun insert(downloadInfos: Collection<DownloadInfo>) {
        return withContext(Dispatchers.IO) {
            return@withContext downloadInfoDao.insert(downloadInfos)
        }
    }
}