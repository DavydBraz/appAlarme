package com.example.appalarm.database

import android.content.Context
import android.util.Log
import com.example.appalarm.model.Alarme

class AlarmeDAO(context: Context) : IAlarmeDAO {
    private val escrita = DataBaseHelper(context).writableDatabase
    private val leitura = DataBaseHelper(context).readableDatabase

    override fun salvar(alarme: Alarme): Int? {
        val nomeNotificacao = alarme.nomeNotificacao
        val hora = alarme.hora
        val data = alarme.data
        val status = if (alarme.status) 1 else 0
        val imagem = alarme.imagem
        val musica = alarme.musica?.let { "'$it'" } ?: "NULL"
        val vibrar = if (alarme.vibrar) 1 else 0
        val todoDia = if (alarme.todoDia) 1 else 0

        val sql = """
        INSERT INTO ${DataBaseHelper.TABELA_ALARMES}
        VALUES (NULL, '$nomeNotificacao', '$hora', '$data', $status, '$imagem', $musica, $vibrar, $todoDia);
    """

        return try {
            escrita.execSQL(sql)
            val idCursor = escrita.rawQuery("SELECT last_insert_rowid()", null)
            val novoId = if (idCursor.moveToFirst()) {
                idCursor.getLong(0).toInt()
            } else {
                null
            }
            idCursor.close()
            Log.i("Info_db", "Sucesso ao inserir os dados")
            novoId
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("Info_db", "Erro ao inserir os dados")
            null
        }
    }


    override fun atualizar(alarme: Alarme): Boolean {
        val idRecebido = alarme.id ?: return false
        val nomeNotificacao = alarme.nomeNotificacao
        val hora = alarme.hora
        val data = alarme.data
        val status = if (alarme.status) 1 else 0
        val imagem = alarme.imagem
        val musica = alarme.musica?.let { "'$it'" } ?: "NULL"
        val vibrar = if (alarme.vibrar) 1 else 0
        val todoDia = if (alarme.todoDia) 1 else 0

        val sql = """
            UPDATE ${DataBaseHelper.TABELA_ALARMES}
            SET ${DataBaseHelper.NOME_NOTIFICACAO} = '$nomeNotificacao',
                ${DataBaseHelper.HORA} = '$hora',
                ${DataBaseHelper.DATA} = '$data',
                ${DataBaseHelper.STATUS} = $status,
                ${DataBaseHelper.IMAGEM} = '$imagem',
                ${DataBaseHelper.MUSICA} = $musica,
                ${DataBaseHelper.VIBRAR} = $vibrar,
                ${DataBaseHelper.TODODIA} = $todoDia
            WHERE ${DataBaseHelper.ID_ALARME} = $idRecebido;
        """

        return try {
            escrita.execSQL(sql)
            Log.i("Info_db", "Sucesso ao atualizar os dados")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("Info_db", "Erro ao atualizar os dados")
            false
        }
    }

    override fun remover(idAlarme: Int): Boolean {
        val sql = """
            DELETE FROM ${DataBaseHelper.TABELA_ALARMES}
            WHERE ${DataBaseHelper.ID_ALARME} = $idAlarme;
        """

        return try {
            escrita.execSQL(sql)
            Log.i("Info_db", "Sucesso ao remover dado")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("Info_db", "Erro ao remover dado")
            false
        }
    }

    override fun listar(): List<Alarme> {
        val sql = "SELECT * FROM ${DataBaseHelper.TABELA_ALARMES};"
        val cursor = leitura.rawQuery(sql, null)
        val listaAlarmes = mutableListOf<Alarme>()

        val indiceId = cursor.getColumnIndex(DataBaseHelper.ID_ALARME)
        val indiceNomeNotif = cursor.getColumnIndex(DataBaseHelper.NOME_NOTIFICACAO)
        val indiceHora = cursor.getColumnIndex(DataBaseHelper.HORA)
        val indiceData = cursor.getColumnIndex(DataBaseHelper.DATA)
        val indiceStatus = cursor.getColumnIndex(DataBaseHelper.STATUS)
        val indiceImagem = cursor.getColumnIndex(DataBaseHelper.IMAGEM)
        val indiceMusica = cursor.getColumnIndex(DataBaseHelper.MUSICA)
        val indiceVibrar = cursor.getColumnIndex(DataBaseHelper.VIBRAR)
        val indiceTodoDia = cursor.getColumnIndex(DataBaseHelper.TODODIA)

        while (cursor.moveToNext()) {
            val idAlarme = cursor.getInt(indiceId)
            val nomeNotificacao = cursor.getString(indiceNomeNotif)
            val hora = cursor.getString(indiceHora)
            val data = cursor.getString(indiceData)
            val status = cursor.getInt(indiceStatus) != 0
            val imagem = cursor.getString(indiceImagem)
            val musica = cursor.getString(indiceMusica)
            val vibrar = cursor.getInt(indiceVibrar) != 0
            val todoDia = cursor.getInt(indiceTodoDia) != 0
            listaAlarmes.add(
                Alarme(idAlarme, nomeNotificacao, hora, data, status, imagem, musica, vibrar, todoDia)
            )
        }
        cursor.close() // Fechar o cursor após o uso
        return listaAlarmes
    }
}
