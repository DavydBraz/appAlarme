package com.example.appalarm.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBaseHelper(context: Context):SQLiteOpenHelper(
    context, "Alarmes.db", null, 1
) {

    companion object{
        val TABELA_ALARMES= "Alarmes"
        val ID_ALARME="id"
        val NOME_NOTIFICACAO="nomeNotificacao"
        val HORA="hora"
        val DATA="data"
        val STATUS="status"
        val IMAGEM="imagem"
        val MUSICA="musica"
        val VIBRAR="vibrar"
        val TODODIA="tododia"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql="CREATE TABLE IF NOT EXISTS $TABELA_ALARMES (\n" +
                "    $ID_ALARME INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    $NOME_NOTIFICACAO TEXT NOT NULL,\n" +
                "    $HORA TEXT NOT NULL,\n" +
                "    $DATA TEXT NOT NULL,\n" +
                "    $STATUS BOOLEAN NOT NULL,\n" +
                "    $IMAGEM TEXT,\n" +
                "    $MUSICA TEXT,\n"+
                "    $VIBRAR TEXT,\n"+
                "    $TODODIA TEXT\n"+
                ");"
        try {
            db?.execSQL(sql)
            Log.i("Info_db", "Sucesso ao criar DB")
        } catch (e: Exception){
            e.printStackTrace()
            Log.i("Info_db", "Erro ao criar db")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}