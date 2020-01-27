package com.kaiserpudding.novel2go.download

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.model.DownloadInfo


import kotlinx.android.synthetic.main.list_item_select_download.view.*

class SelectDownloadAdapter : RecyclerView.Adapter<SelectDownloadAdapter.ViewHolder>() {

    private val downloadInfos: MutableList<DownloadInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_select_download, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val downloadInfo = downloadInfos[position]

        with(holder) {
            url.text = downloadInfo.url
            name.text = downloadInfo.name
            isChapter.isChecked = downloadInfo.isChapter

            view.tag = downloadInfo
            view.setOnClickListener {
                val checkBox = it.findViewById<CheckBox>(R.id.download_is_chapter)
                checkBox.toggle()
                downloadInfos[position].isChapter = checkBox.isSelected
            }
        }
    }

    override fun getItemCount(): Int = downloadInfos.size

    fun addDownloadInfo(info: Collection<DownloadInfo>) {
        downloadInfos.addAll(info)
        notifyDataSetChanged()
    }

    fun getSelected(): List<DownloadInfo> {
        return downloadInfos.filter {
                it.isChapter
            }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val url: TextView = view.download_url
        val name: TextView = view.download_name
        val isChapter: CheckBox = view.download_is_chapter

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }
}
