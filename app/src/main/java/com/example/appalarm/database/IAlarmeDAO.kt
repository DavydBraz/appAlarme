package com.example.appalarm.database

import com.example.appalarm.model.Alarme

interface IAlarmeDAO {
    fun salvar(alarme: Alarme): Int?
    fun atualizar(alarme: Alarme): Boolean
    fun remover(idAlarme: Int): Boolean
    fun listar(): List<Alarme>
}