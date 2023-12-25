package com.simonercole.nine.ui.model

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.ui.model.TimerFormat.timeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Appendable
import java.time.LocalDateTime
import kotlin.properties.Delegates

class NineGameViewModelGauntlet(application: Application) : NineGameViewModel(application = application) {

    var score = MutableLiveData(mutableIntStateOf(0))
    var sessionEnded = MutableLiveData(mutableStateOf(false))
    var totalTime by Delegates.notNull<Int>()
    val attemptsFinished = MutableLiveData(mutableStateOf(false))
    var job: Job? = null
    val _timerValue = MutableLiveData(mutableStateOf(0))
    val _play = MutableLiveData(false)
    private var bestSCore by Delegates.notNull<Int>()
    val newScore = MutableLiveData(0)
    private val gameDB = GameDB.getInstance(application.baseContext)
    private val repository = DBRepo(gameDB.getDAO())
    val bestScoreChanged = MutableLiveData(false)


    override fun setUpGame(diff: String) {
        val backgroundJob = CoroutineScope(Dispatchers.IO).launch {
            bestSCore = if (repository.getMaxScore() != null) {
                repository.getMaxScore()!!
            } else 0
        }
        runBlocking {
            backgroundJob.join()
        }
        _timerValue.value!!.value = 300
        totalTime = _timerValue.value!!.value
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        createKeyboard()
        focusArray.value!![0] =0
        sessionStarted = true
        inputs.value!!.removeAt(0)
    }

    private fun setUpNewGame()  {
        liveInput.value = CharArray(9) { ' ' }
        inputs.value = mutableListOf(HashMap<Int, Pair<String, Char>>())
        focusArray.value = IntArray(9) { 1 * it }
        distanceArray = IntArray(9) { -1 }
        sequenceStatus.value = IntArray(9) { 0 }
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        createKeyboard()
        focusArray.value!![0] =0
        sessionStarted = true
        inputs.value!!.removeAt(0)
    }

    override fun makeGuess() {
        if (firstGuess.value == true) {
            startTimer()
            firstGuess.value = false
            musicStarted.value = true
        }
        musicStarted.value = true
        calculateDistance()
        updateInputs()
        clearInput()
        updateFocusByWrite(0)
        checkGameStatus()
    }

    override fun startTimer() {
        if (_timerValue.value!!.value == 0) _timerValue.value!!.value = 0
        job?.cancel()
        job = GlobalScope.launch (Dispatchers.Main) {
            withContext(Dispatchers.Main){
                delay(timeMillis = 200)
                while (isActive) {
                    if (_timerValue.value!!.value <= 0) {
                        job?.cancel()
                        _play.value = false
                        timerExpired()
                        return@withContext
                    }
                    delay(timeMillis = 1000)
                    _timerValue.value!!.value = _timerValue.value!!.value - 1
                    _play.value = true
                }}

        }
    }

    override fun checkGameStatus() {
            if (sequenceStatus.value!!.contains(0).not()) {
                score.value!!.intValue++
                setUpNewGame()
            }
        }

    override fun saveGameToDB() {
        val game =
            GameGauntlet(
                time = getTimerLabel(totalTime) ,
                dateTime = LocalDateTime.now().toString(),
                score = score.value!!.intValue
            )
        repo.insertGameGauntlet(game)
    }

    override fun timerExpired() {
        sessionEnded.value!!.value = true
        newScore.value = score.value!!.intValue
        if (bestSCore < newScore.value!!) {
            bestSCore = newScore.value!!
            bestScoreChanged.value = true
        }
        saveGameToDB()
    }

    override fun updateInputs() {
        val map =  HashMap<Int, Pair<String, Char>>()
        val newList = mutableListOf(HashMap<Int, Pair<String, Char>>())
        newList.removeAt(0)

        inputs.value!!.forEach { hashMap ->
            newList.add(hashMap)
        }

        for (i in 0..8) {
            map[i]  = Pair(distanceArray[i].toString(), liveInput.value!![i])
        }
        newList.add(map)
        inputs.value  = newList
    }
    fun stopCountDownTimer() {
        isPlaying.value = false
        countDownTimer.cancel()
    }
    fun pause() {
        job?.cancel()
        _play.value = false
    }

    fun stop() {
        job?.cancel()
        _timerValue.value!!.value = 0
        _play.value = true
    }
    fun getTimerLabel(value: Int): String {
        return "${padding(value / 60)} : ${padding(value % 60)}"
    }

    fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

}

@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactoryGauntlet(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModelGauntlet(application) as T
    }
}
