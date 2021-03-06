package com.kaiserpudding.novel2go.repositories

import androidx.lifecycle.LiveData
import com.kaiserpudding.novel2go.database.dao.DownloadDao
import com.kaiserpudding.novel2go.model.Download
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class DownloadRepository(private val downloadDao: DownloadDao) {

    suspend fun insert(download: Download): Long {
        return withContext(Dispatchers.IO + NonCancellable) {
            return@withContext downloadDao.insert(download)
        }
    }

    suspend fun delete(download: Download) {
        withContext(Dispatchers.IO + NonCancellable) {
            downloadDao.delete(download)
        }
    }

    suspend fun delete(downloads: LongArray) {
        withContext(Dispatchers.IO + NonCancellable) {
            downloadDao.delete(downloads)
        }
    }

    fun getAll(): LiveData<List<Download>> {
        return downloadDao.getAll()
    }
}