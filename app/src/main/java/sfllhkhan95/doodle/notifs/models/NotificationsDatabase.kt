package sfllhkhan95.doodle.notifs.models

import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.hawk.Hawk
import sfllhkhan95.doodle.notifs.utils.NotificationListener
import java.io.Serializable
import java.util.*

class NotificationsDatabase private constructor() : Serializable {

    @Transient
    private var notificationListener: NotificationListener? = null

    private val notifications: MutableList<RemoteMessage.Notification>

    val all: List<RemoteMessage.Notification>
        get() = notifications

    init {
        notifications = ArrayList()
    }

    fun add(e: RemoteMessage.Notification) {
        notifications.add(e)
        updateDb()

        // Trigger event notificationListener
        notificationListener?.onNotificationReceived()
    }

    operator fun get(i: Int): RemoteMessage.Notification {
        return notifications[i]
    }

    fun remove(i: Int): RemoteMessage.Notification {
        return notifications.removeAt(i)
    }

    fun size(): Int {
        return notifications.size
    }

    fun clear() {
        notifications.clear()
        updateDb()
    }

    private fun updateDb() {
        db = this
    }

    fun setNotificationListener(notificationListener: NotificationListener) {
        this.notificationListener = notificationListener
    }

    companion object {
        private const val TAG = "NOTIFICATIONS"
        private var db: NotificationsDatabase
            set(value) {
                Hawk.put(TAG, value)
            }
            get() {
                return Hawk.get(TAG, NotificationsDatabase())
            }

        val instance: NotificationsDatabase
            get() = db
    }

}