package com.example.downloadmannager.screens


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.downloader.*
import com.downloader.database.DownloadModel
import com.example.downloadmannager.R
import com.example.downloadmannager.databinding.HomeBinding
import com.example.downloadmannager.utils.Utils
import java.util.*


class Home : Fragment() {

    companion object {
        const val TAG = "Home"
    }

    private lateinit var binding: HomeBinding
//    private val taskAdapter by lazy { TaskAdapter() }
    private var fileId = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home, container, false)


        setViews()

        return binding.root
    }




    private fun setViews() {
        binding.apply {



            btnAddTask.setOnClickListener {
                addTaskDialog()
            }


            download.btnStart.setOnClickListener {
                when(PRDownloader.getStatus(fileId)) {
                    Status.RUNNING -> {PRDownloader.pause(fileId)}
                    Status.PAUSED -> {PRDownloader.resume(fileId)}
                    else -> {}
                }
            }


            download.btnCancel.setOnClickListener {
                PRDownloader.cancel(fileId)
            }


        }
    }



    private fun addTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Uri")
        val input = EditText(requireContext())
        input.hint = "Uri"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val uri = input.text.trim().toString()
            download(uri)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }




    private fun download(uri: String) {
        binding.download.apply {

        val fileFormat = Utils.getFileFormatByUri(uri.toUri(),requireContext()) // for ex: mp4 , jpg , png , application
        val fileType = Utils.getFileTypeByUri(uri.toUri(),requireContext()) // for ex: image / video / audio
        val fileName = "${Calendar.getInstance().time}.$fileFormat"
        val dirPath = Utils.getRootDirPath(requireContext(),fileFormat)


            if (Status.RUNNING === PRDownloader.getStatus(fileId)) {
                PRDownloader.pause(fileId)
                return
            }
            progressBar.isIndeterminate = true
            progressBar.indeterminateDrawable.setColorFilter(
                Color.BLUE, PorterDuff.Mode.SRC_IN
            )

            if (Status.PAUSED === PRDownloader.getStatus(fileId)) {
                PRDownloader.resume(fileId)
                return
            }

           val downloadFile = PRDownloader.download( uri, dirPath, fileName)
                .build()

              downloadFile .setOnStartOrResumeListener {
                   progressBar.isIndeterminate = false
                   btnStart.isEnabled = true
                   btnStart.text = "Pause"
                   btnCancel.isEnabled = true
               }
               .setOnPauseListener { btnStart.setText("Resume") }
               .setOnCancelListener {
                   btnStart.text = "Start"
                   btnCancel.isEnabled = false
                   progressBar.progress = 0
                   progressStatus.text = ""
                   fileId = 0
                   progressBar.isIndeterminate = false
               }
               .setOnProgressListener { prog ->
                   val progressPercent = prog.currentBytes * 100 / prog.totalBytes
                   Log.d(TAG,"progress: $progressPercent")
                   progressBar.progress = progressPercent.toInt()
                   progressStatus.text = Utils.getProgressDisplayLine(prog.currentBytes, prog.totalBytes, progressPercent)
                   progressBar.isIndeterminate = false
               }

               .start( object : OnDownloadListener {
                   override fun onDownloadComplete() {
                       progressBar.isEnabled = false
                       btnCancel.isEnabled = false
                       btnStart.text = "Complete"



                       Log.d(TAG,"downloadFile: \n" +
                               "fileFormat: $fileFormat \n" +
                               " downloadId: ${downloadFile.downloadId} \n" +
                               "fileName: ${downloadFile.fileName} \n"+
                               "downloadedBytes: ${downloadFile.downloadedBytes} \n"+
                               "future: ${downloadFile.future} \n"+
                               "dirPath: ${downloadFile.dirPath} \n"+
                               "connectTimeout: ${downloadFile.connectTimeout} \n"+
                               "readTimeout: ${downloadFile.readTimeout} \n"+
                               "status: ${downloadFile.status} \n"+
                               "url: ${downloadFile.url} \n"+
                               "totalBytes: ${downloadFile.totalBytes} \n"

                       )


                   }

                   override fun onError(error: Error) {
                       btnStart.text = "Start"
                       Log.e(TAG,"server error: ${error.serverErrorMessage}")
                       Log.e(TAG,"connection Exception: ${error.connectionException}")

                       Toast.makeText(requireContext(), "server error: ${error.serverErrorMessage} \n connection error: ${error.connectionException}" + " " + "1", Toast.LENGTH_SHORT).show()
                       progressStatus.text = ""
                       progressBar.progress = 0
                       btnCancel.isEnabled = false
                       progressBar.isIndeterminate = false
                       fileId = 0
                       btnStart.isEnabled = true
                   }



               })





            fileId = downloadFile.downloadId
        }

    }




    fun showMessage(t: String){
        Toast.makeText(requireContext(),t,Toast.LENGTH_SHORT).show()
    }

}