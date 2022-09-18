package com.example.downloadmannager.utils


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import java.io.File

import java.util.*


object Utils {
    private const val TAG = "Home"


    fun getRootDirPath(context: Context,fileFormat:String): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(context.applicationContext, fileFormat).first()
            Log.d(TAG,"media mounted/ path: ${file.absolutePath}")
            file.absolutePath
        } else {
            Log.d(TAG,"not media mounted/ path: ${ context.applicationContext.filesDir.absolutePath}")
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long, progressPercent: Long): String {
        return if (totalBytes.toInt() >= 1 && progressPercent >= 0) {
            getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes) + " $progressPercent%"
        } else if (totalBytes.toInt() <= 0 && progressPercent < 0) {
            getBytesToMBString(currentBytes) + "/" + "?"
        }
        else { // the total bytes is unKnown
            getBytesToMBString(currentBytes) + "/" + "?" + " $progressPercent%"
        }
    }


    private fun getBytesToMBString(bytes: Long): String {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }



    private fun getMimeType(uri: Uri, context: Context): String? {
       val  mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context.applicationContext.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
        return mimeType
    }


    fun getFileFormatByUri(uri: Uri, context: Context): String {
        val fileType = getMimeType(uri, context)
        val format = if (fileType != null){
            val format =  if (fileType.contains('-')) {
                val index = fileType.lastIndexOf('-')
                fileType.substring(index + 1)
            }else{
                val index = fileType.lastIndexOf('/')
                fileType.substring(index.plus(1))
            }
            format
        }else{
            "unKnown"
        }
        return format
    }


    fun getFileTypeByUri(uri: Uri, context: Context): String {
        val fileType = getMimeType(uri, context)
        val type = if (fileType != null){
            val index = fileType.lastIndexOf('/')
            return fileType.substring(0, index)
        }else{
            "unKnown"
        }
        return type
    }

//    fun getFileTypeByUri(uri: Uri, context: Context): String {
//        val fileType = getMimeType(uri, context)
//        val index = fileType.lastIndexOf('/')
//        return fileType.substring(0, index)
//    }



}