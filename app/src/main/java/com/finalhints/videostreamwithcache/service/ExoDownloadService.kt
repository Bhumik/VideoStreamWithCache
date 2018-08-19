package com.finalhints.videostreamwithcache.service

import android.app.Notification
import android.content.Context
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.utils.DownloadNotificationUtil
import com.finalhints.videostreamwithcache.utils.DownloadUtil
import com.google.android.exoplayer2.offline.DownloadAction
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util

class ExoDownloadService protected constructor() : DownloadService(FOREGROUND_NOTIFICATION_ID, DownloadService.DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL, CHANNEL_ID, R.string.exo_download_notification_channel_name) {

    override fun getDownloadManager(): DownloadManager {
        //return ((VideoStreamApplication) getApplication()).getDownloadManager();
        return DownloadUtil.getDownloadManager(this)
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(taskStates: Array<DownloadManager.TaskState>): Notification {
        return DownloadNotificationUtil.buildProgressNotification(
                /* context= */ this,
                R.drawable.exo_controls_play,
                CHANNEL_ID, null,
                "Saving offline",
                taskStates)/* contentIntent= */
    }

    override fun onTaskStateChanged(taskState: DownloadManager.TaskState?) {
        if (taskState!!.action.isRemoveAction) {
            return
        }
        var notification: Notification? = null
        if (taskState.state == DownloadManager.TaskState.STATE_COMPLETED) {
            val notificationId = FOREGROUND_NOTIFICATION_ID + taskState.taskId
            /*
            notification =
                    DownloadNotificationUtil.buildDownloadCompletedNotification(
                            this,
                            R.drawable.exo_controls_play,
                            CHANNEL_ID,
                            null,
                            Util.fromUtf8Bytes(taskState.action.data));
            NotificationUtil.setNotification(this, notificationId, notification);
*/
            NotificationUtil.setNotification(this, notificationId, null)

        } else if (taskState.state == DownloadManager.TaskState.STATE_FAILED) {
            notification = DownloadNotificationUtil.buildDownloadFailedNotification(
                    /* context= */ this,
                    R.drawable.exo_controls_play,
                    CHANNEL_ID, null,
                    Util.fromUtf8Bytes(taskState.action.data))

            val notificationId = FOREGROUND_NOTIFICATION_ID + taskState.taskId
            NotificationUtil.setNotification(this, notificationId, notification)
        }
    }

    companion object {
        fun startService(context: Context, classJava: Class<ExoDownloadService>, downloadAction: DownloadAction, foreground: Boolean) {
            startWithAction(context, classJava, downloadAction, foreground)
        }

        private val CHANNEL_ID = "download_channel"
        private val JOB_ID = 1
        private val FOREGROUND_NOTIFICATION_ID = 121
    }
}
