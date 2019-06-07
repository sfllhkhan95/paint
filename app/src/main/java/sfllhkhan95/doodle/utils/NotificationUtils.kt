package sfllhkhan95.doodle.utils

import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.hawk.Hawk
import sfllhkhan95.doodle.DoodleApplication.Companion.TAG_NOTIFICATIONS
import sfllhkhan95.doodle.utils.listener.NotificationListener
import java.io.Serializable
import java.util.*

class NotificationUtils private constructor() : Serializable {

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
        private var db: NotificationUtils
            set(value) {
                Hawk.put(TAG_NOTIFICATIONS, value)
            }
            get() {
                return Hawk.get(TAG_NOTIFICATIONS, NotificationUtils())
            }

        val instance: NotificationUtils
            get() = db
    }

}