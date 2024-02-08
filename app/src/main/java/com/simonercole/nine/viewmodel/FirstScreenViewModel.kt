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

/**
 * ViewModel responsible for managing the first screen of the application.
 * @param application The application context.
 */
class FirstScreenViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for controlling the visibility of difficulty selection
    private var showDifficulty: MutableLiveData<Boolean> = MutableLiveData(false)
    val observableShowDifficulty: LiveData<Boolean> = showDifficulty

    // LiveData for controlling the opening of the dialog
    private var openDialog = MutableLiveData(true)
    val observableOpenDialog: LiveData<Boolean> = openDialog

    // Game difficulty selected by the user
    private var gameDifficulty = Difficulty.Easy.toString()

    // LiveData indicating whether the difficulty has been chosen
    private var difficultyChosen = MutableLiveData(false)
    val observableDifficultyChosen: LiveData<Boolean> = difficultyChosen

    // LiveData controlling the visibility of the confirmation button
    private var confirmVisibility = MutableLiveData(0f)
    val observableConfirmVisibility: LiveData<Float> = confirmVisibility

    /**
     * Toggles the visibility of the difficulty selection.
     */
    fun setShowDifficulty() {
        showDifficulty.value = showDifficulty.value?.not()
    }

    /**
     * Changes the visibility of the confirmation button.
     */
    fun changeVisibility() {
        confirmVisibility.value = 1f
    }

    /**
     * Handles the change of difficulty.
     * @param difficulty The selected difficulty.
     * @param easyString The string representation of easy difficulty.
     * @param mediumString The string representation of medium difficulty.
     */
    fun changeDifficulty(difficulty: String, easyString: String, mediumString: String) {
        gameDifficulty = when (difficulty) {
            easyString -> Difficulty.Easy.toString()
            mediumString -> Difficulty.Medium.toString()
            else -> Difficulty.Hard.toString()
        }
        if (difficultyChosen.value == false) difficultyChosen.value = difficultyChosen.value?.not()
    }

    /**
     * Handles the closing of the dialog.
     */
    fun handleDialogClosing() {
        showDifficulty.value = false
        openDialog.value = true
        difficultyChosen.value = false
        confirmVisibility.value = 0f
    }

    /**
     * Navigates to the game screen.
     * @param navHostController The navigation controller.
     */
    fun navigateGameScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.SECOND_SCREEN + "/${gameDifficulty}")
    }

    /**
     * Navigates to the played games screen.
     * @param navHostController The navigation controller.
     */
    fun navigateToPlayedGamesScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.PLAYED_GAMES)
    }

    /**
     * Retrieves toast values to show based on the selected difficulty.
     * @param difficulty The selected difficulty.
     * @param easyString The string representation of easy difficulty.
     * @param mediumString The string representation of medium difficulty.
     * @return A Pair containing toast duration and message.
     */
    fun getToastValuesToShow(
        difficulty: String,
        easyString: String,
        mediumString: String
    ): Pair<Int, String> {
        return when (difficulty) {
            easyString -> Pair(4, "01 : 40")
            mediumString -> Pair(4, "01 : 20")
            else -> Pair(3, "00 : 50")
        }
    }


    /**
     * ViewModel Factory for creating FirstScreenViewModel instances.
     * @param application The application context.
     */
    @Suppress("UNCHECKED_CAST")
    class FirstScreenFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FirstScreenViewModel(application) as T
        }
    }
}
