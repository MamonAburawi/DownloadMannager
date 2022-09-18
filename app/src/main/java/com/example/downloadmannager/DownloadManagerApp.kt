package com.example.downloadmannager

import android.app.Application
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig



class DownloadManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(this, config)
    }
}