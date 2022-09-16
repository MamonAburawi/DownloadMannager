package com.example.downloadmannager.screens

import android.Manifest
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.downloader.*
import com.example.downloadmannager.R
import com.example.downloadmannager.Utils
import com.example.downloadmannager.databinding.HomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*


class Home : Fragment() {

    companion object {
        const val TAG = "Home"
    }

    private lateinit var binding: HomeBinding

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


    private fun checkPermission(uri: String,fileName: String) {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {  /* ... */
                    download(uri,fileName)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
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
            val filename = Calendar.getInstance().time.toString()
            checkPermission(uri,filename)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }



    private fun download(uri: String, fileName: String) {
        val dirPath = Utils.getRootDirPath(requireContext())
        binding.download.apply {

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

           val download = PRDownloader.download( uri, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener {
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
                    progressStatus.text = Utils.getProgressDisplayLine(prog.currentBytes, prog.totalBytes,progressPercent)
                    progressBar.isIndeterminate = false
                }
                .start( object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        progressBar.isEnabled = false
                        btnCancel.isEnabled = false
                        btnStart.text = "Complete"
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

            fileId = download
        }

    }



}