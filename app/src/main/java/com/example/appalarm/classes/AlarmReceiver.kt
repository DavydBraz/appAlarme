package com.example.appalarm.classes

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.appalarm.R
import java.io.File

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mensagem = intent.getStringExtra("mensagem") ?: "Alarme disparado!"
        //configurar depois o imagemPath
        val imagemPath = intent.getStringExtra("imagem") ?: "path padrao"
        val vibrar = intent.getBooleanExtra("vibrar", false) // Obtém o valor do booleano
        val alarmeID= intent.getIntExtra("AlarmeID",-1)

        // futuramente vai servir para carregar a imagem com base no caminho passado
        val iconBitmap: Bitmap? = imagemPath.let {
            val imageFile = File(it)
            if (imageFile.exists()) {
                BitmapFactory.decodeFile(it)
            } else {
                Log.i("errado","errado")
                BitmapFactory.decodeResource(context.resources, R.drawable.baseline_notifications_active_24)
            }
        }  ?: BitmapFactory.decodeResource(context.resources, R.drawable.baseline_notifications_active_24)
        //Vai servir para colocar imagem grande de fundo
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(iconBitmap)
        Log.i("AlarmReceiver", "Bitmap carregado: ${iconBitmap != null}")

        val builder = NotificationCompat.Builder(context, AlarmeChannel.CHANNEL_ID)
            .setLargeIcon(iconBitmap) // Definir o ícone grande como a imagem carregada
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Notificação de Alarme")
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(bigPictureStyle)
            //.setSound() -musiquinha

        if (alarmeID == -1) {
            Log.e("AlarmReceiver", "ID do alarme é inválido.")
            return
        }

        if (vibrar) {
            val vibrationPattern = longArrayOf(0, 500, 250, 500)
            builder.setVibrate(vibrationPattern)
        }else{
            builder.setSilent(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
          return
        }

        notificationManager.notify(alarmeID, builder.build())
    }
}
