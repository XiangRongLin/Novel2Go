package com.kaiserpudding.novel2go.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Information about downloaded chapter for toc use.
 *
 * @property url
 * @property name
 * @property isChapter
 */
@Entity(tableName = "download_infos")
data class DownloadInfo(
    val url: String,
    val name: String,
    @ColumnInfo(name = "is_chapter")
    var isChapter: Boolean = false,
    @ColumnInfo(name = "toc_url")
    val tocUrl: String? = null
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.readBoolean()
        } else {
            parcel.readInt() == 1
        },
        parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isChapter)
        } else {
            dest.writeInt(if (isChapter) 1 else 0)
        }
        if (tocUrl != null) dest.writeString(tocUrl)
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