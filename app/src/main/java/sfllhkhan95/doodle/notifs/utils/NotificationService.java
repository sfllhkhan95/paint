package sfllhkhan95.doodle.notifs.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.notifs.models.NotificationsDatabase;

public class NotificationService extends FirebaseMessagingService {

    private static int notificationId = 0;
    private final NotificationCompat.Builder nBuilder;

    public NotificationService() {
        nBuilder = new NotificationCompat.Builder(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            pushNotif(notification);
            NotificationsDatabase.getInstance().add(notification);
        }
    }

    private void pushNotif(RemoteMessage.Notification notification) {
        nBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        nBuilder.setContentTitle(notification.getTitle());
        nBuilder.setContentText(notification.getBody());
        nBuilder.setLights(Color.GREEN, 100, 100);
        nBuilder.setVibrate(new long[]{100, 50, 50, 100, 5000});

        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifier != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (notifier.areNotificationsEnabled()) {
                    notifier.notify(notificationId++, nBuilder.build());
                }
            } else {
                notifier.notify(notificationId++, nBuilder.build());
            }
        }
    }

}
