package com.kaiserpudding.novel2go.download

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.util.multiSelect.MultiSelectFragment
import com.kaiserpudding.novel2go.util.setSafeOnClickListener

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DownloadFragment.OnListFragmentInteractionListener] interface.
 */
class DownloadFragment : MultiSelectFragment<Download, DownloadAdapter>() {
    override val actionMenuId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun onMyActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListInteraction(itemId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private lateinit var downloadViewModel: DownloadViewModel

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel::class.java)
        adapter = DownloadAdapter(listener, this@DownloadFragment)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.download_recycler_view)
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
