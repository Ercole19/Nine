package com.simonercole.nine.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.utils.NineGameUtils
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.difficultyDialogBackground
import com.simonercole.nine.theme.distance_one
import com.simonercole.nine.theme.distance_three
import com.simonercole.nine.theme.distance_two
import com.simonercole.nine.theme.fullInputTile
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel
import com.simonercole.nine.viewmodel.NineGameViewModelFactory
import kotlinx.coroutines.launch


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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SecondScreenPortrait(viewModel: NineGameViewModel, navController: NavHostController, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    val currentInput by viewModel.observableCurrentInput.observeAsState()
    val userAttempts by viewModel.observableAttempts.observeAsState()
    val currentKeyBoard by viewModel.observableCurrentKeyBoard.observeAsState()
    val gameStatus by viewModel.observableGameStatus.observeAsState()
    val userInputs by viewModel.observableUserGuesses.observeAsState()
    val timerValue = viewModel.observableTimerValue.observeAsState()
    val newBestTime by viewModel.observableNewBestTime.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyListState()


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
        AlertDialog(
            backgroundColor = Color(0xfffff8dc),
            onDismissRequest = {

            },
            title = {
                Text(
                    text = if (gameStatus!! == GameStatus.Won) stringResource(id = R.string.WinningTitle) else stringResource(id = R.string.LosingTitle),
                    color = Color.Black,
                    style = AppTheme.typography.body1
                )
            },
            text = {
                Text(
                    text = if (gameStatus!! == GameStatus.Won) stringResource(id = R.string.winningMessage) else stringResource(id = R.string.LosingMessage),
                    style = AppTheme.typography.body1,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                if (gameStatus!! == GameStatus.Lost) {
                    Text(
                        text = stringResource(id = R.string.SequenceWas) + "  " + String(viewModel.getSequenceToGuess()),
                        style = AppTheme.typography.body1,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = AppTheme.dimens.small2)
                    )
                }
                if (newBestTime!!) {
                    Text(
                        text = stringResource(id = R.string.RecordMessage) + "  " +
                                when(viewModel.getDifficulty()) {
                                    Difficulty.Easy -> stringResource(id = R.string.easy_diff)
                                    Difficulty.Medium -> stringResource(id = R.string.medium_diff)
                                    else -> stringResource(id = R.string.hard_diff)
                                }

                                + "  :  " + viewModel.getUserGameTime(),
                        style = AppTheme.typography.body1,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = AppTheme.dimens.small2)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.navigateToSecondScreen(navController)
                    },
                ) {
                    Text(stringResource(id = R.string.PlayAgain), style = AppTheme.typography.body1)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.navigateToMainMenu(navController)
                }) {
                    Text(stringResource(id = R.string.mainMenu), style = AppTheme.typography.body1)
                }
            })
    }

    BackHandler(enabled = true, onBack = {
        if (gameStatus!! == GameStatus.OnGoing) viewModel.quitRequest()
        else viewModel.navigateToMainMenu(navController)

    })

    if (gameStatus!! == GameStatus.Paused) {
        AlertDialog(
            backgroundColor = Color(0xfffff8dc),
            onDismissRequest = {
                viewModel.resumeGame()
            },
            title = {
                Text(
                    text = stringResource(id = R.string.QuittingRequest),
                    style = AppTheme.typography.body1,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleQuitGame()
                        if (viewModel.getEndRequest() == EndRequest.Refresh) viewModel.navigateToSecondScreen(navController)
                        else viewModel.navigateToMainMenu(navController)
                        viewModel.resetGame()
                    },
                ) {
                    Text(stringResource(id = R.string.endGame), style = AppTheme.typography.body1)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resumeGame()
                }) {
                    Text(stringResource(id = R.string.back), style = AppTheme.typography.body1)
                }
            })
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
        val (inputRow, distanceRow, userKeyBoardBox, pastGuessesText, title, refreshButton, timer, backIcon, attempts, pastGuessesBox) = createRefs()
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "arrow back",
            modifier = Modifier
                .constrainAs(backIcon) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin1)
                    start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                }

                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                )
                {
                    if (gameStatus!! == GameStatus.OnGoing) viewModel.quitRequest()
                    else viewModel.navigateToMainMenu(navController)
                }
                .size(AppTheme.dimens.medium1),
            tint = Color.Black
        )

        Icon(

            imageVector = Icons.Default.Refresh,
            contentDescription = "refresh icon",
            modifier = Modifier
                .constrainAs(refreshButton) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin1)
                    end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                }
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                )
                {
                    if (gameStatus!! == GameStatus.OnGoing) viewModel.refreshRequest()
                    else viewModel.navigateToSecondScreen(navController)
                }
                .size(AppTheme.dimens.medium1),
            tint = Color.Black
        )

        Text(
            text = "Nine",
            style = AppTheme.typography.h1,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.smallMargin2)
                    start.linkTo(backIcon.end)
                    end.linkTo(parent.end, ConstraintLayoutMargins.largeMargin)
                }
        )

        Text(
            text = stringResource(id = R.string.attempts) + " : " + userAttempts + "/" + viewModel.getMaxAttempts(),
            style = AppTheme.typography.h6,
            modifier = Modifier
                .constrainAs(attempts) {
                    top.linkTo(refreshButton.bottom, ConstraintLayoutMargins.smallMargin3)
                    end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin3)
                })

        Text(
            text = NineGameUtils.getTimerLabel(timerValue.value!!.value),
            style = AppTheme.typography.h6,
            modifier = Modifier
                .constrainAs(timer) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin1)
                    end.linkTo(refreshButton.start, ConstraintLayoutMargins.smallMargin3)
                })

        Row(
            modifier = Modifier
                .constrainAs(distanceRow) {
                    top.linkTo(parent.top, ConstraintLayoutMargins.buttonHeight)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end) }) {}

        Row(
            modifier = Modifier
                .constrainAs(inputRow) {
                    top.linkTo(distanceRow.bottom, ConstraintLayoutMargins.smallMargin2)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0..8) {
                Box(
                    modifier = Modifier
                        .size(AppTheme.dimens.tileDimensions)
                        .border(
                            if (currentInput!![i].isFocused && !currentInput!![i].isGuessed) 2.dp else 1.dp,
                            color = if (currentInput!![i].isFocused && !currentInput!![i].isGuessed) Color.Red else Color.Black,
                        )
                        .background(
                            if (currentInput!![i].value == ' ') Color.White else fullInputTile
                        )
                        .clickable {
                            if (!currentInput!![i].isGuessed) {
                                viewModel.updateFocusByTouch(i)
                                if (currentInput!![i].value != ' ') viewModel.deleteChar(i)
                            } else viewModel.updateFocusByTouch(i)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    currentInput?.get(i)?.let {
                        Text(
                            text = it.value.toString(),
                            style = AppTheme.typography.h6,
                            color = Color.White
                        )
                    }
                }
            }

        }

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

        Box(modifier = Modifier
            .constrainAs(pastGuessesBox) {
                top.linkTo(pastGuessesText.bottom, ConstraintLayoutMargins.smallMargin3)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(userKeyBoardBox.top, ConstraintLayoutMargins.mediumMargin1)
            }
            .background(Color.White, RoundedCornerShape(AppTheme.dimens.small1))
            .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
            .border(2.dp, Color.Black, RoundedCornerShape(AppTheme.dimens.small1))
            .fillMaxHeight(0.3f)
            .fillMaxWidth(0.95f)
        ) {
            LazyColumn(modifier = Modifier.padding(AppTheme.dimens.small1), state = state) {

                coroutineScope.launch {
                    if (userInputs!!.size > 2) {
                        state.animateScrollToItem(userInputs!!.size - 1)
                    }
                }

                item {
                    if (userInputs!!.isEmpty().not()) {
                        userInputs!!.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (i in 0..8) {
                                    Box(
                                        modifier = Modifier
                                            .size(AppTheme.dimens.smallTileDimensions)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        it[i]?.first.toString().let { it1 ->
                                            Text(
                                                text = it1,
                                                style = AppTheme.typography.h6,
                                                color =
                                                if (it[i]?.first.toString() == "?") Color.Black
                                                else {
                                                    when (it[i]?.first.toString()) {
                                                        "0" -> Color.Green
                                                        "1" -> distance_one
                                                        "2" -> distance_two
                                                        "3" -> distance_three
                                                        else -> Color.Red
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (i in 0..8) {
                                    Box(
                                        modifier = Modifier
                                            .size(AppTheme.dimens.smallTileDimensions)
                                            .border(
                                                1.dp,
                                                color = Color.Black,
                                            )
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = it[i]?.second.toString(),
                                            style = AppTheme.typography.h6,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(AppTheme.dimens.medium1))
                        }

                    }
                }
            }
        }



        Box(modifier = Modifier
            .constrainAs(userKeyBoardBox) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
        ) {


            ConstraintLayout {
                val (inputTop, inputBottom, confirmBtn) = createRefs()

                if (viewModel.isInputFull()) {
                    Card(onClick = {
                        viewModel.makeGuess()
                    },
                        modifier = Modifier
                            .constrainAs(confirmBtn) {
                                top.linkTo(userKeyBoardBox.bottom, ConstraintLayoutMargins.mediumMargin3)
                                start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                                end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(AppTheme.dimens.logoSize, AppTheme.dimens.medium3),
                        backgroundColor = difficultyDialogBackground,
                        shape = RoundedCornerShape(AppTheme.dimens.small3),
                        elevation = AppTheme.dimens.small1
                    )

                    {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            Text(
                                style = AppTheme.typography.h6,
                                text = stringResource(id = R.string.Confirm),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .constrainAs(inputTop) {
                            top.linkTo(userKeyBoardBox.bottom, ConstraintLayoutMargins.mediumMargin1)
                            start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                            end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                            bottom.linkTo(inputBottom.top)
                        }
                        .fillMaxWidth()
                        .padding(start = AppTheme.dimens.small1, end = AppTheme.dimens.small2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    for (i in 0..4) {
                        if (currentKeyBoard!![i].isVisible && !currentKeyBoard!![i].isGuessed) {
                            Box(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .clickable {
                                        viewModel.updateInput(
                                            viewModel.getCurrentFocus(),
                                            currentKeyBoard!![i].value
                                        )
                                    }
                                    .background(
                                        fullInputTile,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )
                                    .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                    .border(
                                        2.dp,
                                        Color.Black,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    ),
                                contentAlignment = Alignment.Center,
                            )
                            {
                                Text(
                                    text = currentKeyBoard!![i].value.toString(),
                                    style = AppTheme.typography.h6,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )
                                    .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                    .border(
                                        2.dp,
                                        Color.Transparent,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .constrainAs(inputBottom) {
                            top.linkTo(inputTop.bottom, ConstraintLayoutMargins.mediumMargin2)
                            start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin3)
                            end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin2)
                            bottom.linkTo(parent.bottom, ConstraintLayoutMargins.smallMargin3)
                        }
                        .fillMaxWidth()
                        .padding(start = AppTheme.dimens.medium2, end = AppTheme.dimens.medium2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    for (i in 5..8) {
                        if (currentKeyBoard!![i].isVisible && !currentKeyBoard!![i].isGuessed
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .clickable {
                                        viewModel.updateInput(
                                            viewModel.getCurrentFocus(),
                                            currentKeyBoard!![i].value
                                        )
                                    }
                                    .background(
                                        fullInputTile,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )
                                    .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                    .border(
                                        2.dp,
                                        Color.Black,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    ),
                                contentAlignment = Alignment.Center,
                            )
                            {
                                Text(
                                    text = currentKeyBoard!![i].value.toString(),
                                    style = AppTheme.typography.h6,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )
                                    .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                    .border(
                                        2.dp,
                                        Color.Transparent,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1)
                                    )) } }
                }

            }
        }
    }
}