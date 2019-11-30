package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.download.service.DownloadService

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewDownloadFragment.OnDownloadInteractionListener] interface
 * to handle interaction events.
 * Use the [NewDownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewDownloadFragment : Fragment() {
    private var listener: OnDownloadInteractionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.button_start)
        button.setOnClickListener {
            val urlEditText = view.findViewById<EditText>(R.id.edit_text_url)
            val url = urlEditText.text.toString()
            Intent(context, DownloadService::class.java).also {
                it.putExtra(DownloadService.DOWNLOAD_URL_INTENT_EXTRA, url)
                activity?.startService(it)
            }
            listener!!.onStartDownload()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_download, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDownloadInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnDownloadInteractionListener")
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
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnDownloadInteractionListener {
        fun onStartDownload()
    }
}
