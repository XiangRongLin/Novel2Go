package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kaiserpudding.novel2go.BuildConfig.EXTERNAL_STORAGE_PERMISSION
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.download.service.DownloadService
import com.kaiserpudding.novel2go.extractor.Extractor
import com.kaiserpudding.novel2go.util.requestStoragePersmission
import kotlinx.android.synthetic.main.fragment_new_download.*
import kotlinx.coroutines.*

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

        requestStoragePersmission()

        val button = view.findViewById<Button>(R.id.button_start)
        button.setOnClickListener {
            val url = edit_text_url.text.toString()
            val isTOCDownload = checkbox_toc_download.isChecked

            if (isTOCDownload) {
                val input = edit_text_wait_between_requests.text
                val waitTime = if (input.isBlank()) 5 else Integer.parseInt(input.toString())
                listener!!.toSelectDownloads(url, waitTime)
            } else {

                val storagePermission = ContextCompat.checkSelfPermission(
                    context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_DENIED

                Intent(context, DownloadService::class.java).also {
                    it.putExtra(DownloadService.DOWNLOAD_URL_INTENT_EXTRA, url)

                    if (isTOCDownload) {
                        it.putExtra(
                            DownloadService.DOWNLOAD_MODE_INTENT_EXTRA,
                            DownloadService.DOWNLOAD_MODE_MULTI
                        )
                    } else {
                        it.putExtra(
                            DownloadService.STORAGE_PERMISSION_INTENT_EXTRA,
                            storagePermission
                        )
                        it.putExtra(
                            DownloadService.DOWNLOAD_MODE_INTENT_EXTRA,
                            DownloadService.DOWNLOAD_MODE_SINGLE
                        )
                    }
                    activity?.startService(it)
                }
                listener!!.onStartDownload()
            }
            hideKeyboardAndDefocus(view.windowToken)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_download, container, false)
    }

    /**
     * Clear focus of quizInputEditText and hide keyboard
     *
     * @param view
     */
    private fun hideKeyboardAndDefocus(windowToken: IBinder) {
        edit_text_url.clearFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
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
        fun toSelectDownloads(url: String, waitTime: Int)
    }
}
