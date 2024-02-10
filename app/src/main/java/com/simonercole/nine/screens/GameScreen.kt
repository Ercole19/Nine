package com.simonercole.nine.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.customwidgets.gamescreen.EndGameDialog
import com.simonercole.nine.customwidgets.gamescreen.GameTopAppBar
import com.simonercole.nine.customwidgets.gamescreen.PastGuessesBox
import com.simonercole.nine.customwidgets.gamescreen.PauseGameDialog
import com.simonercole.nine.customwidgets.gamescreen.UserInput
import com.simonercole.nine.customwidgets.gamescreen.UserKeyBoard
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel
import com.simonercole.nine.viewmodel.NineGameViewModelFactory

@SuppressLint("RememberReturnType")
@Composable
fun SecondScreen(difficulty: String, navController: NavHostController) {
    val myEnum: Difficulty = Difficulty.valueOf(difficulty)
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val viewModel : NineGameViewModel = viewModel(factory = NineGameViewModelFactory(context.applicationContext as Application))
    if (viewModel.getGameStatus().toString() == "NotStarted") {
        viewModel.setUpGame(myEnum)
    }
    SecondScreenPortrait(viewModel = viewModel, navController = navController)
}

@SuppressLint("UnrememberedMutableInteractionSource", "CoroutineCreationDuringComposition")
@Composable
fun SecondScreenPortrait(viewModel: NineGameViewModel, navController: NavHostController, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {
    val currentInput by viewModel.observableCurrentInput.observeAsState()
    val userAttempts by viewModel.observableAttempts.observeAsState()
    val currentKeyBoard by viewModel.observableCurrentKeyBoard.observeAsState()
    val gameStatus by viewModel.observableGameStatus.observeAsState()
    val userInputs by viewModel.observableUserGuesses.observeAsState()
    val timerValue = viewModel.observableTimerValue.observeAsState()
    val newBestTime by viewModel.observableNewBestTime.observeAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (gameStatus!! == GameStatus.OnGoing) viewModel.userChangeActivityMidGame()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (gameStatus!! == GameStatus.Paused) viewModel.resumeGame()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (gameStatus!! == GameStatus.Won || gameStatus!! == GameStatus.Lost) {
        EndGameDialog(viewModel, gameStatus, newBestTime, navController)
    }

    BackHandler(enabled = true, onBack = {
        if (gameStatus!! == GameStatus.OnGoing) viewModel.quitRequest()
        else viewModel.navigateToMainMenu(navController)

    })

    if (gameStatus!! == GameStatus.Paused) {
       PauseGameDialog(viewModel, navController)
    } else if (gameStatus!! == GameStatus.NotStarted) {
        viewModel.navigateToSecondScreen(navController)
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ninegameboard),
            contentDescription = "Nine game background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        ConstraintLayoutMargins.SetConstraintMargins()
        val (inputRow, userKeyBoardBox, pastGuessesText, topAppBar, pastGuessesBox) = createRefs()

        GameTopAppBar(viewModel, gameStatus!!, navController, userAttempts!!, timerValue.value!!,
            modifier = Modifier
                .constrainAs(topAppBar) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin3)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(inputRow.top)
                }
                .fillMaxWidth())

        UserInput(modifier = Modifier.constrainAs(inputRow)
        {
            top.linkTo(topAppBar.bottom, ConstraintLayoutMargins.smallMargin2)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(pastGuessesText.top)
        }, currentInput = currentInput!!, viewModel = viewModel)

        Text(
            text = stringResource(id = R.string.PastGuesses),
            style = AppTheme.typography.h6,
            modifier = Modifier
                .constrainAs(pastGuessesText) {
                    top.linkTo(inputRow.bottom, ConstraintLayoutMargins.mediumMargin2)
                    start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                    bottom.linkTo(pastGuessesBox.top)
                }
        )

        PastGuessesBox(modifier = Modifier.constrainAs(pastGuessesBox) {
            top.linkTo(pastGuessesText.bottom, ConstraintLayoutMargins.smallMargin3)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(userKeyBoardBox.top, ConstraintLayoutMargins.mediumMargin1)
        }, userInputs = userInputs!!)

        UserKeyBoard(
            modifier = Modifier.constrainAs(userKeyBoardBox) {
                top.linkTo(pastGuessesBox.bottom, ConstraintLayoutMargins.smallMargin3)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, ConstraintLayoutMargins.mediumMargin1)
            },
            viewModel = viewModel ,
            currentKeyBoard = currentKeyBoard!!
        )
    }
}