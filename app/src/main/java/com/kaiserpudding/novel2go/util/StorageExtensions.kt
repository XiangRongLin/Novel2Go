package com.kaiserpudding.novel2go.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kaiserpudding.novel2go.BuildConfig
import java.io.File

fun Fragment.requestStoragePersmission() {
    ActivityCompat.requestPermissions(
        activity!!,
        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
        BuildConfig.EXTERNAL_STORAGE_PERMISSION
    )
}

fun Fragment.getStorageFile(): File {
    val storagePermission = ContextCompat.checkSelfPermission(
        context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_DENIED
    return if (storagePermission) File(
        Environment.getExternalStorageDirectory(),
        "Documents/Novel2Go"
    )
    else context!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
}