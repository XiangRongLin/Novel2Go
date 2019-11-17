package com.kaiserpudding.novel2go.download


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.download.DownloadFragment.OnListFragmentInteractionListener
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.util.multiSelect.MultiSelectAdapter

/**
 * [RecyclerView.Adapter] that can display a [Download] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class DownloadAdapter(
    private val mListener: OnListFragmentInteractionListener?,
    multiSelectListener: MultiSelectAdapterItemInteractionListener
) : MultiSelectAdapter<Download>(multiSelectListener) {
    override val viewHolderId = R.id.download_item_title

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectViewHolder {
        return createViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_view_item_download,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultiSelectViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.textView.text = list[position].title
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

}
