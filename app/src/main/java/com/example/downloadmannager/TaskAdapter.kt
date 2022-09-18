//package com.example.downloadmannager
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.Context
//import android.text.InputType
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.core.net.toUri
//import  androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.downloader.*
//import com.downloader.database.DownloadModel
//import com.example.downloadmannager.databinding.ItemDownloadBinding
//import com.example.downloadmannager.utils.Utils
//import java.util.*
//
//
//@SuppressLint("NotifyDataSetChanged")
//class TaskAdapter() :ListAdapter<DownloadModel, TaskAdapter.ViewHolder>(TaskDiffUtil()) {
//
//    lateinit var taskClickListener: TaskListener
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = getItem(position)
//
//        holder.bind(item,taskClickListener)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder.from(parent)
//    }
//
//    class ViewHolder private constructor(private val binding: ItemDownloadBinding,private val context: Context) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind( data: DownloadModel, taskClickListener: TaskListener) {
//            binding.apply {
//
//                /** button start **/
//                btnStart.setOnClickListener {
//                    when (PRDownloader.getStatus(data.id)) {
//                        Status.PAUSED-> { taskClickListener.onResume(data.id,adapterPosition) }
//                        Status.RUNNING-> { taskClickListener.onPause(data.id,adapterPosition) }
//                        Status.FAILED -> {}
//                        else -> {}
//                    }
//                }
//
//
//                /** button cancel **/
//                btnCancel.setOnClickListener {
//                    taskClickListener.onCancel(data.id,adapterPosition)
//                }
//
//            }
//        }
//
//
//
//
//
//
//
//
//
//
//        companion object {
//
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = ItemDownloadBinding.inflate(layoutInflater, parent, false)
//
//                return ViewHolder(binding, parent.context)
//            }
//
//
//        }
//
//    }
//
//
//    fun download(uri: String, context: Context, position: Int) {
//
//        val fileFormat = Utils.getFileFormatByUri(uri.toUri(), context) // for ex: mp4 , jpg , png , application
//        val fileType = Utils.getFileTypeByUri(uri.toUri(), context) // for ex: image / video / audio
//        val fileName = "${Calendar.getInstance().time}.$fileFormat"
//        val dirPath = Utils.getRootDirPath(context, fileFormat)
//
////        binding.progressBar.isIndeterminate = true
////        binding.progressBar.indeterminateDrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
//
//        val downloadFile = PRDownloader.download(uri, dirPath, fileName)
//            .build()
//
//
//
//        downloadFile.apply {
//            setOnStartOrResumeListener { taskClickListener.onStart(downloadFile.url) }
//            setOnPauseListener { taskClickListener.onPause(downloadId,position) }
//            setOnCancelListener { taskClickListener.onCancel(downloadId,position)}
//            setOnProgressListener { taskClickListener.onProgress(downloadId,position,it) }
//            start(object : OnDownloadListener {
//
//                override fun onDownloadComplete() { taskClickListener.onCancel(downloadId,position)}
//                override fun onError(error: Error) {taskClickListener.onError(downloadId,position,error)}
//
//            })
//        }
//
//        currentList.add(po)
//
//    }
//
//
//
//    fun downLoad(uri: String){
//        taskClickListener.onStart(uri)
//        notifyDataSetChanged()
//    }
//
//
//    fun resume(id: Int,index: Int) {
//        PRDownloader.resume(id)
//        notifyItemChanged(index)
//    }
//
//
//    fun pause(id: Int,index: Int) {
//        PRDownloader.pause(id)
//        notifyItemChanged(index)
//    }
//
//
//    fun cancel(id: Int,index: Int) {
//        PRDownloader.cancel(id)
//        notifyItemChanged(index)
//    }
//
//    fun cancelAll(id: Int) {
//        PRDownloader.cancel(id)
//        notifyDataSetChanged()
//    }
//
//    fun cleanUp(id: Int,index: Int,days: Int) {
//        PRDownloader.cleanUp(days)
//        notifyItemChanged(index)
//    }
//
//
//    fun addTask(downloadModel: DownloadModel){
//        currentList.add(downloadModel)
//        notifyDataSetChanged()
//    }
//
//    fun removeTask(index: Int){
//        currentList.removeAt(index)
//        notifyItemRemoved(index)
//    }
//
//
//
////
////    private fun setViews(uri: String, binding: ItemDownloadBinding) {
////        binding.apply {
////
////
////
////
////            resumeDownload() // for resume download
////
////            progressBar.isIndeterminate = true
////            progressBar.indeterminateDrawable.setColorFilter(
////                Color.BLUE, PorterDuff.Mode.SRC_IN
////            )
////
////
////
////            val downloadFile = PRDownloader.download(uri, dirPath, fileName)
////                .build()
////
////            downloadFile.setOnStartOrResumeListener {
////                progressBar.isIndeterminate = false
////                btnStart.isEnabled = true
////                btnStart.text = "Pause"
////                btnCancel.isEnabled = true
////            }
////                .setOnPauseListener { btnStart.setText("Resume") }
////                .setOnCancelListener {
////                    btnStart.text = "Start"
////                    btnCancel.isEnabled = false
////                    progressBar.progress = 0
////                    progressStatus.text = ""
////                    fileId = 0
////                    progressBar.isIndeterminate = false
////                }
////                .setOnProgressListener { prog ->
////                    val progressPercent = prog.currentBytes * 100 / prog.totalBytes
////                    Log.d(TAG, "progress: $progressPercent")
////                    progressBar.progress = progressPercent.toInt()
////                    progressStatus.text = Utils.getProgressDisplayLine(
////                        prog.currentBytes,
////                        prog.totalBytes,
////                        progressPercent
////                    )
////                    progressBar.isIndeterminate = false
////                }
////
////                .start(object : OnDownloadListener {
////                    override fun onDownloadComplete() {
////                        progressBar.isEnabled = false
////                        btnCancel.isEnabled = false
////                        btnStart.text = "Complete"
////
////
////
////                        Log.d(
////                            TAG, "downloadFile: \n" +
////                                    "fileFormat: $fileFormat \n" +
////                                    " downloadId: ${downloadFile.downloadId} \n" +
////                                    "fileName: ${downloadFile.fileName} \n" +
////                                    "downloadedBytes: ${downloadFile.downloadedBytes} \n" +
////                                    "future: ${downloadFile.future} \n" +
////                                    "dirPath: ${downloadFile.dirPath} \n" +
////                                    "connectTimeout: ${downloadFile.connectTimeout} \n" +
////                                    "readTimeout: ${downloadFile.readTimeout} \n" +
////                                    "status: ${downloadFile.status} \n" +
////                                    "url: ${downloadFile.url} \n" +
////                                    "totalBytes: ${downloadFile.totalBytes} \n"
////
////                        )
////
////
////                    }
////
////                    override fun onError(error: Error) {
////                        btnStart.text = "Start"
////                        Log.e(TAG, "server error: ${error.serverErrorMessage}")
////                        Log.e(TAG, "connection Exception: ${error.connectionException}")
////
////                        Toast.makeText(
////                            requireContext(),
////                            "server error: ${error.serverErrorMessage} \n connection error: ${error.connectionException}" + " " + "1",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                        progressStatus.text = ""
////                        progressBar.progress = 0
////                        btnCancel.isEnabled = false
////                        progressBar.isIndeterminate = false
////                        fileId = 0
////                        btnStart.isEnabled = true
////                    }
////
////
////                })
////        }
////    }
//
//
//
//    class TaskDiffUtil : DiffUtil.ItemCallback<DownloadModel>() {
//        override fun areItemsTheSame(oldItem: DownloadModel, newItem: DownloadModel): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        @SuppressLint("DiffUtilEquals")
//        override fun areContentsTheSame(oldItem: DownloadModel, newItem: DownloadModel): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//
//
//    interface TaskListener {
//        fun onStart(uri: String)
//        fun onCancel(taskId: Int, index: Int)
//        fun onResume(taskId: Int, index: Int)
//        fun onPause(taskId: Int, index: Int)
//        fun onProgress(taskId: Int, index: Int, progress: Progress)
//        fun onComplete(taskId: Int,index: Int)
//        fun onError(taskId: Int, index: Int, error: Error)
//    }
//
//
//
//}
//
//
