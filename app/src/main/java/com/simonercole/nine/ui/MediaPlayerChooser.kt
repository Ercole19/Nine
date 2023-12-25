package com.simonercole.nine.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.simonercole.nine.R


class MediaPlayerChooser {
    companion object {
        lateinit var gameMusic: MediaPlayer
        lateinit var lossMusic: MediaPlayer
        lateinit var victoryMusic: MediaPlayer


        @Composable
        fun initMusic() {
            val context = LocalContext.current
            gameMusic = MediaPlayer.create(context, R.raw.zeldone)
            gameMusic.isLooping = true
            lossMusic = MediaPlayer.create(context, R.raw.lose)
            victoryMusic = MediaPlayer.create(context, R.raw.win)
        }
    }




}