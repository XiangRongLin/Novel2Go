package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kaiserpudding.novel2go.BuildConfig.APPLICATION_ID
import com.kaiserpudding.novel2go.BuildConfig.DEBUG
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

    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var recyclerView: RecyclerView

    private var listener: OnListFragmentInteractionListener? = null

    override val actionModeCallback: MultiSelectActionModeCallback =
        object : MultiSelectActionModeCallback() {
            override val actionMenuId: Int = R.menu.selection_delete_menu

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                Log.d(LOG_TAG, "Action item clicked with mode $mode and item $item")
                return when (item.itemId) {
                    R.id.action_delete -> {
                        if (DEBUG) Log.d(LOG_TAG, "Action item 'delete' clicked")
                        downloadViewModel.delete(adapter.selectedIdArray)
                        actionMode?.finish()
                        true
                    }
                    else -> false
                }
            }
        }

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

    private fun startEmailIntent(file: File) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val email = prefs.getString("kindle_email", "")
        if (!email.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            val useConvert = prefs.getBoolean("use_amazon_convert", true)
            if (useConvert) intent.putExtra(Intent.EXTRA_SUBJECT, "convert")
            val uri = FileProvider.getUriForFile(
                context!!,
                "$APPLICATION_ID.fileprovider",
                file
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "message/rfc/822"
            startActivity(Intent.createChooser(intent, "send mail"))
        }
    }

    override fun onListInteraction(position: Int) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                context!!,
                "$APPLICATION_ID.fileprovider",
                adapter.list[position].file
            ), "application/pdf"
        )
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    override fun onOptionsInteraction(position: Int) {
        val popupMenu = PopupMenu(
            context,
            (recyclerView.findViewHolderForLayoutPosition(position) as DownloadAdapter.DownloadViewHolder).optionsView
        )
        popupMenu.inflate(R.menu.menu_recycler_view_item_download)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.send_to_kindle -> {
                    startEmailIntent(
                        adapter.list[position].file
                    )
                    true
                }
                R.id.open_with -> {
                    Log.d("popupMenu", "Open with clicked")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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

    companion object {
        private const val LOG_TAG = "DownloadFragment"
    }
}
