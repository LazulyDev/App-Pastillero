package ifp.android.pastillero

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

// clase que será usada para el lanzamiento de notificaciones
class ReminderReciber: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val nombreMed = intent?.getStringExtra("Pastilla") ?: "Medicamentos"
        val channelId = "MED_REMINDERS_ID"

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // crear el canal por el que se va a notificar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de Medicación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para toma de medicación"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // ajustes de la notificación
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("Recordatorio de Medicación")
            .setContentText("Es momento de tomar: $nombreMed")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // lanzamiento de la notificación
        notificationManager.notify(nombreMed.hashCode(), builder.build())
    }
}