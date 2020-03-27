package com.kaiserpudding.novel2go.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kaiserpudding.novel2go.database.AppDatabase
import com.kaiserpudding.novel2go.extractor.Extractor
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.model.DownloadInfo
import com.kaiserpudding.novel2go.repositories.DownloadInfoRepository
import com.kaiserpudding.novel2go.repositories.DownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadRepository: DownloadRepository =
        DownloadRepository(AppDatabase.getDatabase(application).downloadDao())
    private val downloadInfoRepository: DownloadInfoRepository by lazy {
        DownloadInfoRepository(AppDatabase.getDatabase(application).downloadInfoDao())
    }
    private val extractor by lazy { Extractor() }
    val allDownloads: LiveData<List<Download>>
        get() = downloadRepository.getAll()

    fun insert(download: Download) = viewModelScope.launch {
        downloadRepository.insert(download)
    }

    fun delete(downloads: LongArray) = viewModelScope.launch {
        downloadRepository.delete(downloads)
    }

    fun extractChaptersFromUrl(url: String, regex: String = ""): LiveData<List<DownloadInfo>> {
        val result = MutableLiveData<List<DownloadInfo>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val downloadInfos: List<DownloadInfo> = if (regex.isEmpty()) {
                    extractor.extractDownloadInfos(url)
                } else {
                    extractor.extractDownloadInfos(url, Regex(regex))
                }
                result.postValue(downloadInfos)
            }
        }
        return result
    }

    /**
     * TODO Move to service
     *
     * @param downloadInfos
     * @param file
     */
    fun download(downloadInfos: List<DownloadInfo>, file: File, waitTime: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val channel = extractor.extractMulti(downloadInfos, file, waitTime)
                for (pair in channel) {
                    insert(Download(
                        pair.second,
                        file.absolutePath,
                        pair.first,
                        System.currentTimeMillis()
                    ))
                }
            }
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                downloadInfoRepository.insert(downloadInfos)
            }
        }
    }
}