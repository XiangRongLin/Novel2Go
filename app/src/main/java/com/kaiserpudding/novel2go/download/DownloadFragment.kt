package com.kaiserpudding.novel2go.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.kaiserpudding.novel2go.BuildConfig
import com.kaiserpudding.novel2go.R
import com.kaiserpudding.novel2go.extractor.Extractor
import com.kaiserpudding.novel2go.model.Download
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DownloadFragment.OnDownloadInteractionListener] interface
 * to handle interaction events.
 * Use the [DownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadFragment : Fragment() {
    private var listener: OnDownloadInteractionListener? = null
    private val downloadViewModel: DownloadViewModel by lazy {
        ViewModelProviders.of(this).get(DownloadViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.button_start)
        button.setOnClickListener {
            val urlEditText = view.findViewById<EditText>(R.id.edit_text_url)
            val url = urlEditText.text.toString()
            urlEditText.setText("")
            GlobalScope.launch {
                val file = activity?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val extractor = Extractor()
                val fileName = extractor.extractSingle(url, file!!)
                val tmp =  File(file, "$fileName.pdf")
                downloadViewModel.insert(
                    Download(
                        fileName,
                        file.absolutePath,
                        url,
                        tmp.length(),
                        System.currentTimeMillis()
                    )
                )
                startEmailIntent(file, fileName)
            }
        }
    }

    private fun startEmailIntent(file: File, fileName: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val email = prefs.getString("kindle_email", "")
        if (!email.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            val useConvert = prefs.getBoolean("use_amazon_convert", true)
            if (useConvert) intent.putExtra(Intent.EXTRA_SUBJECT, "convert")
            val uri = FileProvider.getUriForFile(
                context!!,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                File(file, "$fileName.pdf")
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "message/rfc/822"
            startActivity(Intent.createChooser(intent, "send mail"))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false)
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

    }
}
