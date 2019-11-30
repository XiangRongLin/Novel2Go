package com.kaiserpudding.novel2go.download.service

import android.app.IntentService
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.kaiserpudding.novel2go.BuildConfig.DEBUG
import com.kaiserpudding.novel2go.download.DownloadViewModel
import com.kaiserpudding.novel2go.extractor.Extractor
import com.kaiserpudding.novel2go.model.Download
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class DownloadService : IntentService("DownloadService") {
    private val extractor: Extractor = Extractor()
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onHandleIntent(intent: Intent?) {
        scope.launch {
            val url = intent!!.getStringExtra(DOWNLOAD_URL_INTENT_EXTRA)

            if (DEBUG) Log.d(LOG_TAG, "onHandleIntent() called with $url")

            val file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val fileName = extractor.extractSingle(url, file!!)
            insertDownload(
                Download(
                    fileName,
                    file.absolutePath,
                    url,
                    File(file, "$fileName.pdf").length(),
                    System.currentTimeMillis()
                )
            )
        }
    }

    private fun insertDownload(download: Download) {
        if (DEBUG) Log.d(LOG_TAG, "insertDownload() called with $download")
        DownloadViewModel(application).insert(download)
    }

    companion object {
        private const val LOG_TAG = "DownloadService"
        const val DOWNLOAD_URL_INTENT_EXTRA = "download_url"
    }
}