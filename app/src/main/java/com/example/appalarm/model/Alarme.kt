package com.example.appalarm.model

import java.io.Serializable

data class Alarme(
    var id: Int?,
    var nomeNotificacao:String,
    var hora:String,
    var data:String,
    var status:Boolean,
    var imagem:String,
    var musica:String?,
    var vibrar:Boolean,
    var todoDia:Boolean
):Serializable
