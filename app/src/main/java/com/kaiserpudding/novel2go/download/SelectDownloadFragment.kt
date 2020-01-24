
package com.kaiserpudding.novel2go.download

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.kaiserpudding.novel2go.BuildConfig
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.util.getStorageFile
import com.kaiserpudding.novel2go.util.requestStoragePersmission

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [SelectDownloadFragment.OnListFragmentInteractionListener] interface.
 */
class SelectDownloadFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var url: String
    private lateinit var downloadViewModel: DownloadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safeArgs: SelectDownloadFragmentArgs by navArgs()
        url = safeArgs.url

        downloadViewModel = ViewModelProviders.of(activity!!).get(DownloadViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_download, container, false)

        val adapter = SelectDownloadAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.select_download_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val finish: Button = view.findViewById(R.id.button_select_finish)
        finish.setOnClickListener {

            downloadViewModel.download(adapter.getSelected(), getStorageFile())
            listener!!.onSelectFinish()
        }

        val liveData = downloadViewModel.extractChaptersFromUrl(url)
        liveData.observe(this, Observer {
            adapter.addDownloadInfo(it)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestStoragePersmission()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
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
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onSelectFinish()
    }
}
