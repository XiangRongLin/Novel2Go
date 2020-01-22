package com.kaiserpudding.novel2go.download

import android.os.Parcel
import android.os.Parcelable

data class DownloadInfo(
    val url: String,
    val name: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(name)
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