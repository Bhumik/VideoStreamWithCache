package com.finalhints.videostreamwithcache.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.DownloadManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

public final class DownloadNotificationUtil {

    private static final @StringRes
    int NULL_STRING_ID = 0;

    private DownloadNotificationUtil() {
    }

    /**
     * Returns a progress notification for the given task states.
     *
     * @param context       A context for accessing resources.
     * @param smallIcon     A small icon for the notification.
     * @param channelId     The id of the notification channel to use. Only required for API level 26 and
     *                      above.
     * @param contentIntent An optional content intent to send when the notification is clicked.
     * @param message       An optional message to display on the notification.
     * @param taskStates    The task states.
     * @return The notification.
     */
    public static Notification buildProgressNotification(
            Context context,
            @DrawableRes int smallIcon,
            String channelId,
            @Nullable PendingIntent contentIntent,
            @Nullable String message,
            DownloadManager.TaskState[] taskStates) {
        float totalPercentage = 0;
        int downloadTaskCount = 0;
        boolean allDownloadPercentagesUnknown = true;
        boolean haveDownloadedBytes = false;
        boolean haveDownloadTasks = false;
        boolean haveRemoveTasks = false;
        for (DownloadManager.TaskState taskState : taskStates) {
            if (taskState.state != DownloadManager.TaskState.STATE_STARTED
                    && taskState.state != DownloadManager.TaskState.STATE_COMPLETED) {
                continue;
            }
            if (taskState.action.isRemoveAction) {
                haveRemoveTasks = true;
                continue;
            }
            haveDownloadTasks = true;
            if (taskState.downloadPercentage != C.PERCENTAGE_UNSET) {
                allDownloadPercentagesUnknown = false;
                totalPercentage += taskState.downloadPercentage;
            }
            haveDownloadedBytes |= taskState.downloadedBytes > 0;
            downloadTaskCount++;
        }

        int titleStringId =
                haveDownloadTasks
                        ? com.google.android.exoplayer2.ui.R.string.exo_download_downloading
                        : (haveRemoveTasks ? com.google.android.exoplayer2.ui.R.string.exo_download_removing : NULL_STRING_ID);
        NotificationCompat.Builder notificationBuilder =
                newNotificationBuilder(
                        context, smallIcon, channelId, contentIntent, message, titleStringId);

        int progress = 0;
        boolean indeterminate = true;
        if (haveDownloadTasks) {
            progress = (int) (totalPercentage / downloadTaskCount);
            indeterminate = allDownloadPercentagesUnknown && haveDownloadedBytes;
        }
        notificationBuilder.setProgress(/* max= */ 100, progress, indeterminate);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setShowWhen(false);
        return notificationBuilder.build();
    }

    /**
     * Returns a notification for a completed download.
     *
     * @param context       A context for accessing resources.
     * @param smallIcon     A small icon for the notifications.
     * @param channelId     The id of the notification channel to use. Only required for API level 26 and
     *                      above.
     * @param contentIntent An optional content intent to send when the notification is clicked.
     * @param message       An optional message to display on the notification.
     * @return The notification.
     */
    public static Notification buildDownloadCompletedNotification(
            Context context,
            @DrawableRes int smallIcon,
            String channelId,
            @Nullable PendingIntent contentIntent,
            @Nullable String message) {
        int titleStringId = com.google.android.exoplayer2.ui.R.string.exo_download_completed;
        return newNotificationBuilder(
                context, smallIcon, channelId, contentIntent, message, titleStringId)
                .build();
    }

    /**
     * Returns a notification for a failed download.
     *
     * @param context       A context for accessing resources.
     * @param smallIcon     A small icon for the notifications.
     * @param channelId     The id of the notification channel to use. Only required for API level 26 and
     *                      above.
     * @param contentIntent An optional content intent to send when the notification is clicked.
     * @param message       An optional message to display on the notification.
     * @return The notification.
     */
    public static Notification buildDownloadFailedNotification(
            Context context,
            @DrawableRes int smallIcon,
            String channelId,
            @Nullable PendingIntent contentIntent,
            @Nullable String message) {
        @StringRes int titleStringId = com.google.android.exoplayer2.ui.R.string.exo_download_failed;
        return newNotificationBuilder(
                context, smallIcon, channelId, contentIntent, message, titleStringId)
                .build();
    }

    private static NotificationCompat.Builder newNotificationBuilder(
            Context context,
            @DrawableRes int smallIcon,
            String channelId,
            @Nullable PendingIntent contentIntent,
            @Nullable String message,
            @StringRes int titleStringId) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId).setSmallIcon(smallIcon);
        if (titleStringId != NULL_STRING_ID) {
            notificationBuilder.setContentTitle(context.getResources().getString(titleStringId));
        }
        if (contentIntent != null) {
            notificationBuilder.setContentIntent(contentIntent);
        }
        if (message != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }
        return notificationBuilder;
    }
}
