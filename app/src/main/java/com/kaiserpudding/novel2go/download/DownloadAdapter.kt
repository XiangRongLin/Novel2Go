package com.kaiserpudding.novel2go.download


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.download.DownloadAdapter.DownloadAdapterInteractionListener
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.util.multiSelect.MultiSelectAdapter
import com.kaiserpudding.novel2go.util.setSafeOnClickListener

/**
 * [RecyclerView.Adapter] that can display a [Download] and makes a call to the
 * specified [DownloadAdapterInteractionListener].
 */
class DownloadAdapter(
    private val listener: DownloadAdapterInteractionListener?,
    multiSelectListener: MultiSelectAdapterItemInteractionListener
) : MultiSelectAdapter<Download>(multiSelectListener) {
    override val viewHolderId = R.id.download_item_title

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectViewHolder {
        return DownloadViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_view_item_download,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultiSelectViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as DownloadViewHolder).titleView.text = list[position].title
    }

    inner class DownloadViewHolder(view: View) :
        MultiSelectViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.download_item_title)
        val optionsView: TextView = view.findViewById(R.id.download_item_options)

        init {
            optionsView.setSafeOnClickListener {
                listener!!.onOptionsInteraction(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        //TODO Avoid try catch
        return try {
            list.size
        } catch (exception: UninitializedPropertyAccessException) {
            0
        }
    }

    fun setDownloads(downloads: List<Download>) {
        list = downloads
        notifyDataSetChanged()
    }

    interface DownloadAdapterInteractionListener {

        fun onOptionsInteraction(position: Int)

    }

}
