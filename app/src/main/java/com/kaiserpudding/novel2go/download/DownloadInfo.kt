package com.kaiserpudding.novel2go.download

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

data class DownloadInfo(
    val url: String,
    val name: String,
    var isChapter: Boolean = false
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.readBoolean()
        } else {
            parcel.readInt() == 1
        }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isChapter)
        } else {
            dest.writeInt(if (isChapter) 1 else 0)
        }
    }

    override fun describeContents(): Int {
        return hashCode()
    }

    companion object CREATOR : Parcelable.Creator<DownloadInfo> {
        override fun createFromParcel(parcel: Parcel): DownloadInfo {
            return DownloadInfo(parcel)
        }

        override fun newArray(size: Int): Array<DownloadInfo?> {
            return arrayOfNulls(size)
        }
    }
}