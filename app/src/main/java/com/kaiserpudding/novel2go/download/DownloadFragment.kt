package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
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
import kotlin.collections.ArrayList

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
                    R.id.action_send_to_kindle -> {
                        if (DEBUG) Log.d(LOG_TAG, "Action item 'send to kindle' clicked")
                        startEmailIntent(adapter.selectedPosition)
                        true
                    }
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

        recyclerView = view.findViewById(R.id.download_recycler_view)
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

    override fun onListInteraction(position: Int) {
        startOpenFileIntent(position)
    }

    override fun onOptionsInteraction(position: Int) {
        val popupMenu = PopupMenu(
            context,
            (recyclerView.findViewHolderForLayoutPosition(position) as DownloadAdapter.DownloadViewHolder).optionsView
        )
        popupMenu.inflate(R.menu.menu_recycler_view_item_download)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    Log.d(LOG_TAG, "Share clicked")
                    startShareIntent(position)
                    true
                }
                R.id.send_to_kindle -> {
                    Log.d(LOG_TAG, "Send email clicked")
                    startEmailIntent(intArrayOf(position))
                    true
                }
                R.id.open_with -> {
                    Log.d(LOG_TAG, "Open with clicked")
                    startOpenFileIntent(position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun startShareIntent(position: Int) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                context!!,
                "$APPLICATION_ID.fileprovider",
                adapter.list[position].file
            ), "application/pdf"
        )
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun startEmailIntent(positions: IntArray) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val email = prefs.getString("kindle_email", "")
        if (!email.isNullOrEmpty()) {
            val intent: Intent
            if (positions.size == 1) {
                intent = Intent(Intent.ACTION_SEND)
                val uri = FileProvider.getUriForFile(
                    context!!,
                    "$APPLICATION_ID.fileprovider",
                    adapter.list[positions[0]].file
                )
                intent.putExtra(Intent.EXTRA_STREAM, uri)
            } else {
                intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                val uris = ArrayList<Uri>()
                positions.forEach { position ->
                    uris.add(
                        FileProvider.getUriForFile(
                            context!!,
                            "$APPLICATION_ID.fileprovider",
                            adapter.list[position].file
                        )
                    )
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            }
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            val useConvert = prefs.getBoolean("use_amazon_convert", true)
            if (useConvert) intent.putExtra(Intent.EXTRA_SUBJECT, "convert")

            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent, "send mail"))
        }
    }

    private fun startOpenFileIntent(position: Int) {
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
