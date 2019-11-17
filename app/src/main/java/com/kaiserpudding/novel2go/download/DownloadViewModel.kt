package com.kaiserpudding.novel2go.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kaiserpudding.novel2go.database.AppDatabase
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.repositories.DownloadRepository
import kotlinx.coroutines.launch

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DownloadRepository =
        DownloadRepository(AppDatabase.getDatabase(application).downloadDao())
    val allDownloads: LiveData<List<Download>> = repository.getAll()

    fun insert(download: Download) = viewModelScope.launch {
        repository.insert(download)
    }
}