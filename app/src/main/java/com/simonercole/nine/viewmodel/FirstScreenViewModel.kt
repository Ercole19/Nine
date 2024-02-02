package com.simonercole.nine.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.utils.NineGameUtils

class FirstScreenViewModel(application: Application) : AndroidViewModel(application) {

    val showDifficulty : MutableLiveData<Boolean> = MutableLiveData(false)
    val openDialog = MutableLiveData(true)
    var gameDifficulty = MutableLiveData(NineGameUtils.Difficulty.Easy.toString())
    val difficultyChosen = MutableLiveData(mutableStateOf(false) )
    val confirmVisibility = MutableLiveData(mutableFloatStateOf(0f) )

    fun setShowDifficulty()  {
        showDifficulty.value = showDifficulty.value!!.not()
    }

    fun changeVisibility() { confirmVisibility.value!!.floatValue = 1f}
    fun changeDifficulty(difficulty: String, easyString : String, mediumString : String) {
        gameDifficulty.value = when (difficulty) {
            easyString -> NineGameUtils.Difficulty.Easy.toString()
            mediumString -> NineGameUtils.Difficulty.Medium.toString()
            else -> NineGameUtils.Difficulty.Hard.toString()
    }
        difficultyChosen.value!!.value = difficultyChosen.value!!.value.not()
}
    fun resetValues() {
        showDifficulty.value = false
        openDialog.value = true
        difficultyChosen.value!!.value = false
        confirmVisibility.value!!.value = 0f
    }


@Suppress("UNCHECKED_CAST")
class FirstScreenFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return FirstScreenViewModel(application) as T
    }
}
}