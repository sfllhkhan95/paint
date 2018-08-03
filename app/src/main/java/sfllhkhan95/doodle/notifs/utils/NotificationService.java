package sfllhkhan95.doodle.notifs.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.notifs.models.NotificationsDatabase;

/**
 * @author saifkhichi96
 * @version 1.1.0
 */
public class NotificationService extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "PRIMARY_CHANNEL";
    private static int notificationId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            pushNotif(notification);
            NotificationsDatabase.getInstance().add(notification);
        }
    }

    /**
     * @param notifier
     * @since 3.5.1
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(@NonNull NotificationManager notifier) {
        CharSequence adminChannelName = getString(R.string.notifications_main_channel_name);
        String adminChannelDescription = getString(R.string.notifications_main_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        notifier.createNotificationChannel(adminChannel);
    }

    private void pushNotif(RemoteMessage.Notification notification) {
        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifier != null) {
            // Setting up Notification channels for Android Oreo and above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels(notifier);
            }

            NotificationCompat.Builder nBuilder = new NotificationCompat
                    .Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setLights(Color.GREEN, 100, 100)
                    .setVibrate(new long[]{100, 50, 50, 100, 5000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                notifier.notify(notificationId++, nBuilder.build());
            } else if (notifier.areNotificationsEnabled()) {
                notifier.notify(notificationId++, nBuilder.build());
            }
        }
    }

}
