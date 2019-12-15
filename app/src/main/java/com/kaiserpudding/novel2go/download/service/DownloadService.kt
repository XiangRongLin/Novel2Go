package com.kaiserpudding.novel2go.download.service

import android.app.IntentService
import android.content.ComponentName
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
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
            val mode = intent.getStringExtra(DOWNLOAD_MODE_INTENT_EXTRA)
            val storagePermission = intent.getBooleanExtra(STORAGE_PERMISSION_INTENT_EXTRA, false)

            if (DEBUG) Log.d(
                LOG_TAG, "onHandleIntent() called with " +
                        "url = $url, mode = $mode, storagePermissions = $storagePermission"
            )

            val file =
                if (storagePermission) File(
                    Environment.getExternalStorageDirectory(),
                    "Documents/Novel2Go"
                )
                else baseContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            when (mode) {
                DOWNLOAD_MODE_SINGLE -> {
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
                DOWNLOAD_MODE_MULTI -> {
                    val channel = extractor.extractMulti(url, file!!)
                    for (pair in channel) {
                        insertDownload(
                            Download(
                                pair.second, //file name
                                file.absolutePath,
                                pair.first, // url of download
                                File(file, "${pair.second}.pdf").length(),
                                System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }
    }


    private fun insertDownload(download: Download) {
        if (DEBUG) Log.d(LOG_TAG, "insertDownload() called with $download")
        DownloadViewModel(application).insert(download)
    }

    companion object {
        private const val LOG_TAG = "DownloadService"
        const val DOWNLOAD_URL_INTENT_EXTRA = "download_url"
        const val STORAGE_PERMISSION_INTENT_EXTRA = "external_storage_persmisison"
        const val DOWNLOAD_MODE_INTENT_EXTRA = "donwload_mode"
        const val DOWNLOAD_MODE_SINGLE = "download_mode_single"
        const val DOWNLOAD_MODE_MULTI = "download_mode_multi"
    }

}