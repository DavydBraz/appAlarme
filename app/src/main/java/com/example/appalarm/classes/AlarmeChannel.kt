import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class AlarmeChannel(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "ALARME_CHANNEL_ID"
        const val CHANNEL_NAME = "Alarme Notifications"
        const val CHANNEL_DESCRIPTION = "Canal para notificações de alarme"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
