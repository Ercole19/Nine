package com.simonercole.nine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.Routes

class FirstScreenViewModel(application: Application) : AndroidViewModel(application) {

    private var showDifficulty : MutableLiveData<Boolean> = MutableLiveData(false)
    val observableShowDifficulty : LiveData<Boolean> = showDifficulty

    private var openDialog = MutableLiveData(true)
    val observableOpenDialog : LiveData<Boolean> = openDialog

    private var gameDifficulty = Difficulty.Easy.toString()

    private var difficultyChosen = MutableLiveData(false )
    val observableDifficultyChosen : LiveData<Boolean> = difficultyChosen

    private var confirmVisibility = MutableLiveData(0f )
    val observableConfirmVisibility : LiveData<Float> = confirmVisibility

    fun setShowDifficulty()  {
        showDifficulty.value = showDifficulty.value?.not()
    }

    fun changeVisibility() { confirmVisibility.value = 1f}
    fun changeDifficulty(difficulty: String, easyString : String, mediumString : String) {
        gameDifficulty = when (difficulty) {
            easyString -> Difficulty.Easy.toString()
            mediumString -> Difficulty.Medium.toString()
            else -> Difficulty.Hard.toString()
    }
        if (difficultyChosen.value == false )difficultyChosen.value = difficultyChosen.value?.not()
}
     fun handleDialogClosing() {
        showDifficulty.value = false
        openDialog.value = true
        difficultyChosen.value = false
        confirmVisibility.value = 0f
    }

    fun navigateGameScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.SECOND_SCREEN + "/${gameDifficulty}")
    }

    fun navigateToPlayedGamesScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.PLAYED_GAMES)
    }

    fun getToastValuesToShow(difficulty: String, easyString: String, mediumString: String) : Pair<Int, String> {
        return when(difficulty) {
             easyString -> Pair(4, "01 : 40")
             mediumString -> Pair(4, "01 : 20")
             else -> Pair(3, "00 : 50")
        }
    }

@Suppress("UNCHECKED_CAST")
class FirstScreenFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return FirstScreenViewModel(application) as T
    }
}
}