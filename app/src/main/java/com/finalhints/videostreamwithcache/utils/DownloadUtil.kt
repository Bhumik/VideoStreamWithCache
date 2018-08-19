package com.finalhints.videostreamwithcache.utils

import android.content.Context
import android.os.Environment

import com.finalhints.videostreamwithcache.R
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util

import java.io.File

object DownloadUtil {
    private var cache: SimpleCache? = null
    private var downloadManager: DownloadManager? = null

    fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            val cacheDirectory = getCacheFolder(context)
            cache = SimpleCache(cacheDirectory, NoOpCacheEvictor())
        }
        return cache!!
    }

    fun getCacheFolder(context: Context): File {
        //return new File(context.getExternalFilesDir(null), "downloads");//ExoDownloads
        return File(context.cacheDir, "media")
    }

    /**
     * method to return named cache folder in our cache directory
     * @param context
     * @param name
     * @return
     */
    fun getCacheFolder(context: Context, name: String): File {
        //return File(context.getExternalFilesDir(null), name);//ExoDownloads
        return File(context.cacheDir, name)
    }

    fun clearCache(context: Context) {
        //getCacheFolder(context).delete();
        for (file in getCacheFolder(context).listFiles()) {
            file.delete()
        }
    }

    private fun getDownloadDirectory(context: Context): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    }

    private fun getDownloadDirectory11(context: Context): File {
        var downloadDirectory = context.getExternalFilesDir(null)
        if (downloadDirectory == null) {
            downloadDirectory = context.filesDir
        }
        return downloadDirectory
    }

    fun getDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            val actionFile = File(getDownloadDirectory(context), "actionAbc.mp4")
            downloadManager = DownloadManager(
                    getCache(context),
                    DefaultDataSourceFactory(
                            context,
                            Util.getUserAgent(context, context.getString(R.string.app_name))),
                    actionFile,
                    ProgressiveDownloadAction.DESERIALIZER)
        }
        return downloadManager!!
    }
}
