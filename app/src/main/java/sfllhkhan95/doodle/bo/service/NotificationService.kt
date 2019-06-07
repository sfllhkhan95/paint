package sfllhkhan95.doodle.bo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import sfllhkhan95.doodle.DoodleApplication.Companion.DEFAULT_NOTIFICATION_CHANNEL

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.NotificationUtils

/**
 * @author saifkhichi96
 * @version 1.1.0
 */
class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val notification = remoteMessage!!.notification
        if (notification != null) {
            pushNotif(notification)
            NotificationUtils.instance.add(notification)
        }
    }

    /**
     * @param notifier
     * @since 3.5.1
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notifier: NotificationManager) {
        val adminChannelName = getString(R.string.notifications_main_channel_name)
        val adminChannelDescription = getString(R.string.notifications_main_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notifier.createNotificationChannel(adminChannel)
    }

    private fun pushNotif(notification: RemoteMessage.Notification) {
        val notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Setting up Notification channels for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notifier)
        }

        val nBuilder = NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setLights(Color.GREEN, 100, 100)
                .setVibrate(longArrayOf(100, 50, 50, 100, 5000))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            notifier.notify(notificationId++, nBuilder.build())
        } else if (notifier.areNotificationsEnabled()) {
            notifier.notify(notificationId++, nBuilder.build())
        }
    }

    companion object {
        private var notificationId = 0
    }

}
