package com.example.appalarm.classes
import android.content.Context
import android.media.MediaPlayer
import com.example.appalarm.R


class SomAlarme(private val context: Context) {
    private var mediaPlayer: MediaPlayer?=null

    fun startAlarm(): MediaPlayer? {
        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer.create(context, R.raw.audiodbopening).apply {
                isLooping=true
                start()
            }

        }else{
            mediaPlayer?.start()
        }
       return mediaPlayer
    }

    fun stopAlarm() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.reset()
            player.release()
            mediaPlayer = null
        }
    }

}
