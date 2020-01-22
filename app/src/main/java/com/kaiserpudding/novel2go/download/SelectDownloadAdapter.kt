package com.kaiserpudding.novel2go.download

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.kaiserpudding.novel2go.R


import com.kaiserpudding.novel2go.download.SelectDownloadFragment.OnListFragmentInteractionListener
import com.kaiserpudding.novel2go.download.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.list_item_select_download.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class SelectDownloadAdapter(
    private val list: Array<DownloadInfo>
) : RecyclerView.Adapter<SelectDownloadAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_select_download, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val downloadInfo = list[position]

        with(holder) {
            url.text = downloadInfo.url
            name.text = downloadInfo.name

            view.tag = downloadInfo
            view.setOnClickListener {
                val checkBox = it.findViewById<CheckBox>(R.id.download_checkbox)
                checkBox.toggle()
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val url: TextView = view.download_url
        val name: TextView = view.download_name

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }
}
