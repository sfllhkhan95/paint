package sfllhkhan95.doodle.notifs.models;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pk.aspirasoft.core.db.PersistentValue;
import sfllhkhan95.doodle.notifs.utils.NotificationListener;

public class NotificationsDatabase implements Serializable {

    private static final String TAG = "NOTIFICATIONS";
    private static PersistentValue<NotificationsDatabase> db;

    private transient NotificationListener notificationListener;

    private final List<RemoteMessage.Notification> notifications;

    private NotificationsDatabase() {
        notifications = new ArrayList<>();
    }

    public static NotificationsDatabase getInstance() {
        if (db == null) {
            db = new PersistentValue<>(TAG, NotificationsDatabase.class);
        }

        if (db.getValue() == null) {
            db.setValue(new NotificationsDatabase());
        }

        return db.getValue();
    }

    public void add(RemoteMessage.Notification e) {
        notifications.add(e);
        updateDb();

        // Trigger event notificationListener
        if (notificationListener != null) {
            notificationListener.onNotificationReceived();
        }
    }

    public RemoteMessage.Notification get(int i) {
        return notifications.get(i);
    }

    public List<RemoteMessage.Notification> getAll() {
        return notifications;
    }

    public RemoteMessage.Notification remove(int i) {
        return notifications.remove(i);
    }

    public int size() {
        return notifications.size();
    }

    public void clear() {
        notifications.clear();
        updateDb();
    }

    private void updateDb() {
        if (db == null) return;
        db.setValue(this);
        db.save();
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

}
