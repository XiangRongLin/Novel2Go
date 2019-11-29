package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kaiserpudding.novel2go.BuildConfig
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.util.multiSelect.MultiSelectFragment
import com.kaiserpudding.novel2go.util.setSafeOnClickListener
import java.io.File

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DownloadFragment.OnListFragmentInteractionListener] interface.
 */
class DownloadFragment : MultiSelectFragment<Download, DownloadAdapter>(),
    DownloadAdapter.DownloadAdapterInteractionListener {

    override fun onOptionsInteraction(position: Int) {
        val popupMenu = PopupMenu(
            context,
            (recyclerView.findViewHolderForLayoutPosition(position) as DownloadAdapter.DownloadViewHolder).optionsView
        )
        popupMenu.inflate(R.menu.menu_recycler_view_item_download)
        popupMenu.show()

    }

    override val actionMenuId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun onMyActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListInteraction(position: Int) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                context!!,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                File(adapter.list[position].path, adapter.list[position].title + ".pdf")
            ), "application/pdf"
        )
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }


    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var recyclerView: RecyclerView

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel::class.java)
        adapter = DownloadAdapter(this, this@DownloadFragment)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.download_recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set the adapter
        downloadViewModel.allDownloads.observe(this@DownloadFragment, Observer { downloads ->
            adapter.setDownloads(downloads)
        })

        view.findViewById<FloatingActionButton>(R.id.new_download_fab).setSafeOnClickListener {
            listener!!.onNewDownloadInteraction()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnListFragmentInteractionListener {
        fun onNewDownloadInteraction()
    }

}
