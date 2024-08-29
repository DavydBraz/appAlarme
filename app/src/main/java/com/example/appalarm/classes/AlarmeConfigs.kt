package com.example.appalarm.classes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appalarm.model.Alarme
import java.util.Calendar

class AlarmeConfigs {

    fun cancelarAlarme(id: Int?, context: Context, alarmManager: AlarmManager){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("AlarmeID", id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id?:0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun calendarioAlarme(alarme: Alarme): Calendar {
        val tempo = alarme.hora
        val partesTemp = tempo.split(':', ' ')
        val minutos = partesTemp.getOrNull(1)?.toIntOrNull() ?: 0
        val horas = partesTemp.getOrNull(0)?.toIntOrNull() ?: 0

        val amPm = when (partesTemp.getOrNull(2)) {
            "AM" -> Calendar.AM
            "PM" -> Calendar.PM
            else -> -1
        }

        val data = alarme.data
        val partesData = data.split('/')
        val dia = partesData.getOrNull(0)?.toIntOrNull() ?: 1
        val mes = partesData.getOrNull(1)?.toIntOrNull()?.minus(1) ?: 0
        val ano = partesData.getOrNull(2)?.toIntOrNull() ?: 2024

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, ano)
            set(Calendar.MONTH, mes)
            set(Calendar.DAY_OF_MONTH, dia)
            set(Calendar.MINUTE, minutos)
            set(Calendar.SECOND, 0)

            if (amPm != -1) {
                set(Calendar.HOUR, if (horas == 12) 0 else horas)
                set(Calendar.AM_PM, amPm)
            } else {
                set(Calendar.HOUR_OF_DAY, horas)
            }
        }
        return calendar
    }

    @SuppressLint("ScheduleExactAlarm")
    fun usarAlarme(context: Context, alarme: Alarme, calendar: Calendar, alarmManager: AlarmManager){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("mensagem", "Alarme: ${alarme.nomeNotificacao}")
            putExtra("imagem", alarme.imagem)
            putExtra("vibrar", alarme.vibrar)
            putExtra("AlarmeID", alarme.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarme.id ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarme.todoDia) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis+10000,
                pendingIntent
            )
//            Log.i("Executou", "Alarme reativado com ID ${alarme.id}")
        }
    }
}