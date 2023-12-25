package com.simonercole.nine.ui.model

import android.app.Application
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

lateinit var countDownTimer : CountDownTimer

abstract class NineGameViewModel(application: Application): AndroidViewModel(application) {

    lateinit var sequenceToGuess: CharArray

    var firstGuess = MutableLiveData(true)
    var musicStarted = MutableLiveData(false)


    private val gameDB = GameDB.getInstance(application.baseContext)
    open val repo = DBRepo(gameDB.getDAO())

    val inputs =  MutableLiveData(mutableListOf(HashMap<Int, Pair<String, Char>>()))

    var liveInput = MutableLiveData(CharArray(9) { ' ' })
    var focusArray = MutableLiveData(IntArray(9) { 1 * it })
    var distanceArray = IntArray(9) { -1 }
    var showConfirm = MutableLiveData(false)
    val isPlaying = mutableStateOf(false)

    var sessionStarted = false
    var startingKeyboard = CharArray(9) { ' ' }
    var sequenceStatus = MutableLiveData(IntArray(9) { 0 })
    var guessedChars = CharArray(9) { ' ' }

    abstract fun setUpGame(diff: String)
    abstract fun makeGuess()
    abstract fun startTimer()
    abstract fun checkGameStatus()
    abstract fun saveGameToDB()
    abstract fun timerExpired()

    fun createKeyboard() {
        for ((i) in sequenceToGuess.withIndex()) {
            startingKeyboard[i] = sequenceToGuess[i]
        }
        startingKeyboard.shuffle()
    }

    fun createSequenceToGuess(): CharArray {
        return NineGameUtils.symbols.toList().shuffled().take(9).joinToString("").toCharArray()
    }

    fun updateInput(index: Int, char: Char) {
        val newInput = CharArray(9) { ' ' }
        liveInput.value?.forEachIndexed { i, _ ->
            if (index == i) newInput[i] = char
            else newInput[i] = liveInput.value!![i]
        }
        liveInput.value = newInput
        val focusIndex = focusArray.value?.indexOf(0)
        if (focusIndex != null) {
            for (idx in focusIndex ..8) {
                if (sequenceStatus.value!![idx] == 0) {
                    updateFocusByWrite(idx)
                    break
                }

            }
        }
        showConfirm.value = liveInput.value?.contains(' ') == false

    }

    abstract fun updateInputs()

    fun resetMusic(mediaPlayer: MediaPlayer) {
        musicStarted.value = false
        mediaPlayer.stop()
    }

    fun clearInput() {
        val newInput = CharArray(9) { ' ' }
        for (i in 0..8) {
            if (sequenceStatus.value?.get(i) == 1) newInput[i] = liveInput.value!![i]
            else newInput[i] = ' '
        }
        liveInput.value = newInput
        showConfirm.value = false
    }

    fun calculateDistance() {
        liveInput.value?.forEachIndexed { index, c ->
            val correctIndex = sequenceToGuess.indexOf(c)
            val finalDistance: Int
            val rawDistance = kotlin.math.abs(correctIndex - index)
            finalDistance = when (rawDistance) {
                in 0..4 -> rawDistance
                else -> 9 - rawDistance
            }
            distanceArray[index] = finalDistance
            if (finalDistance == 0) sequenceStatus.value?.set(index, 1)
        }
    }

    fun deleteChar(index: Int) {
        val newInput = CharArray(9) { ' ' }
        liveInput.value?.forEachIndexed { i, _ ->
            if (index == i) newInput[i] = ' '
            else newInput[i] = liveInput.value!![i]
        }
        liveInput.value = newInput
        if (showConfirm.value == true) showConfirm.value = false
    }

    fun updateFocusByTouch(index:Int) {
        val newInput = IntArray(9) { 1 }
        var newIndex: Int = index
        if (sequenceStatus.value!![index] == 0) {
            newInput[newIndex] = 0
            focusArray.value = newInput
        }
        else {updateFocusByWrite(index)}

    }

    fun updateFocusByWrite(index: Int) {
        val newInput = IntArray(9) { 1 }
        var newIndex: Int = index
        while (true) {
            if (liveInput.value?.contains(' ')?.not() == true) break
            if (liveInput.value?.get(newIndex) != ' ' || sequenceStatus.value?.get(newIndex) == 1) {
                if (newIndex + 1 > 8) {
                    newIndex = 0
                } else {
                    newIndex++
                }
            } else {
                newInput[newIndex] = 0
                break
            }
        }
        focusArray.value = newInput
    }
}

object TimerFormat {
    private const val FORMAT = "%02d:%02d"

    fun Long.timeFormat(): String = String.format(
        FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}