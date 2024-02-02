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
import com.simonercole.nine.utils.NineGameUtils.GameStatus
import com.simonercole.nine.utils.Routes
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.viewmodel.NineGameViewModel
import com.simonercole.nine.viewmodel.NineGameViewModelFactory
import kotlinx.coroutines.launch


@SuppressLint("RememberReturnType")
@Composable
fun SecondScreen(difficulty: String, navController: NavHostController) {
    val myEnum: NineGameUtils.Difficulty = NineGameUtils.Difficulty.valueOf(difficulty)
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val viewModel : NineGameViewModel = viewModel(factory = NineGameViewModelFactory(context.applicationContext as Application))
    if (viewModel.gameStatus.value!!.toString() == "NotStarted") {
        viewModel.setUpGame(myEnum)
    }
    SecondScreenPortrait(viewModel = viewModel, navController = navController)
}

@SuppressLint("UnrememberedMutableInteractionSource", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SecondScreenPortrait(viewModel: NineGameViewModel, navController: NavHostController, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {
    val focusArray by viewModel.focusArray.observeAsState()
    val liveInput by viewModel.liveInput.observeAsState()
    val userAttempts by viewModel.currentAttempts.observeAsState()
    val sequenceStatus by viewModel.sequenceStatus.observeAsState()
    val gameStatus by viewModel.gameStatus.observeAsState()
    val userInputs by viewModel.userGuesses.observeAsState()
    val timerValue = viewModel.timerValue.observeAsState()
    val newBestTime by viewModel.newBestTime.observeAsState()
    val errorFromDB by viewModel.errorFromDB.observeAsState()
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

    if (errorFromDB!!) {
        AlertDialog(
            backgroundColor = Color(0xfffff8dc),
            onDismissRequest = {
                viewModel.resetGame()
                navController.navigate(Routes.NINE_START)
            },
            title = {
                Text(
                    text = stringResource(id = R.string.ErrorFromDB),
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
                )},
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetGame()
                        navController.navigate(Routes.NINE_START)
                    },
                ) {
                    androidx.compose.material3.Text(stringResource(id = R.string.Confirm), style = AppTheme.typography.body1)
                }
            })

    }

    if (gameStatus!! == GameStatus.Won || gameStatus!! == GameStatus.Lost) {
        AlertDialog(
            backgroundColor = Color(0xfffff8dc),
            onDismissRequest = {
                viewModel.resetGame()
                navController.navigate(Routes.NINE_START)
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
                        text = stringResource(id = R.string.SequenceWas) + "  " + String(viewModel.sequenceToGuess),
                        style = AppTheme.typography.body1,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = AppTheme.dimens.small2)
                    )
                }
                if (newBestTime!!) {
                    Text(
                        text = stringResource(id = R.string.RecordMessage) + "  " +
                                when(viewModel.difficulty) {
                                    NineGameUtils.Difficulty.Easy -> stringResource(id = R.string.easy_diff)
                                    NineGameUtils.Difficulty.Medium -> stringResource(id = R.string.medium_diff)
                                    else -> stringResource(id = R.string.hard_diff)
                                }

                                + "  :  " + viewModel.userGameTime.value,
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
                        viewModel.resetGame()
                        navController.navigate(Routes.SECOND_SCREEN + "/${viewModel.difficulty}")
                    },
                ) {
                    androidx.compose.material3.Text(stringResource(id = R.string.PlayAgain), style = AppTheme.typography.body1)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    navController.navigate(Routes.NINE_START)
                }) {
                    androidx.compose.material3.Text(stringResource(id = R.string.mainMenu), style = AppTheme.typography.body1)
                }
            })
    }

    BackHandler(enabled = true, onBack = {
        if (gameStatus!! == GameStatus.OnGoing) viewModel.quitRequest()
        else navController.navigate(Routes.NINE_START)

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
                        viewModel.getTime()
                        viewModel.saveGameToDB()
                        if (viewModel.endRequestFromUser == NineGameUtils.EndRequest.Refresh) navController.navigate(
                            Routes.SECOND_SCREEN + "/${viewModel.difficulty}"
                        )
                        else navController.navigate(Routes.NINE_START)
                        viewModel.resetGame()
                    },
                ) {
                    androidx.compose.material3.Text(stringResource(id = R.string.endGame), style = AppTheme.typography.body1)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resumeGame()
                }) {
                    androidx.compose.material3.Text(stringResource(id = R.string.back), style = AppTheme.typography.body1)
                }
            })
    } else if (gameStatus!! == GameStatus.NotStarted) {
        navController.navigate(Routes.SECOND_SCREEN + "/${viewModel.difficulty}")
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.gambacc),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        ConstraintLayoutMargins.SetConstraintMargins()
        val (firstRow, distanceRow, testBox, text, title, refreshButton, timer, backIcon, attempts, seqBox) = createRefs()
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "info",
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
                    else navController.navigate(Routes.NINE_START)
                }
                .size(AppTheme.dimens.medium1),
            tint = Color.Black
        )

        Icon(

            imageVector = Icons.Default.Refresh,
            contentDescription = "info",
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
                    else navController.navigate(Routes.SECOND_SCREEN + "/${viewModel.difficulty}")
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
            text = stringResource(id = R.string.attempts) + " : " + userAttempts + "/" + viewModel.maxAttempts,
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
                    end.linkTo(parent.end)
                }

        ) {
        }

        Row(
            modifier = Modifier
                .constrainAs(firstRow) {
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
                            if (focusArray?.get(i) == 0 && sequenceStatus?.get(i) == 0) 2.dp else 1.dp,
                            color = if (focusArray?.get(i) == 0 && sequenceStatus?.get(i) == 0) Color.Red else Color.Black,
                        )
                        .background(if (liveInput!![i] == ' ') Color.White else Color(0xffA47449))
                        .clickable {
                            if (sequenceStatus?.get(i) == 0) {
                                viewModel.updateFocusByTouch(i)
                                viewModel.deleteChar(i)
                            } else viewModel.updateFocusByTouch(i)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    liveInput?.get(i)?.let {
                        Text(
                            text = it.toString(),
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
                .constrainAs(text) {
                    top.linkTo(firstRow.bottom, ConstraintLayoutMargins.mediumMargin1)
                    start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                }
        )

        Box(modifier = Modifier
            .constrainAs(seqBox) {
                top.linkTo(text.bottom, ConstraintLayoutMargins.smallMargin3)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(testBox.top, ConstraintLayoutMargins.mediumMargin1)
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
                                                        "1" -> Color(0xff014462)
                                                        "2" -> Color(0xffadd8e6)
                                                        "3" -> Color(0xffFFA500)
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
            .constrainAs(testBox) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
        ) {


            ConstraintLayout {
                val (inputTop, inputBottom, confirm) = createRefs()

                if (!liveInput!!.contains(' ')) {
                    Card(onClick = {
                        viewModel.makeGuess()
                    },
                        modifier = Modifier
                            .constrainAs(confirm) {
                                top.linkTo(testBox.bottom, ConstraintLayoutMargins.mediumMargin3)
                                start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                                end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(AppTheme.dimens.logoSize, AppTheme.dimens.medium3),
                        backgroundColor = Color(0xfff8f1e7),
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
                            top.linkTo(testBox.bottom, ConstraintLayoutMargins.mediumMargin2)
                            start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                            end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                        }
                        .fillMaxWidth()
                        .padding(start = AppTheme.dimens.small1, end = AppTheme.dimens.small2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    for (i in 0..4) {
                        if (liveInput?.contains(viewModel.startingKeyboard[i])
                                ?.not() == true && viewModel.guessedChars.contains(viewModel.startingKeyboard[i])
                                .not()
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .clickable {
                                        focusArray
                                            ?.indexOf(0)
                                            ?.let {
                                                viewModel.updateInput(
                                                    it,
                                                    viewModel.startingKeyboard[i]
                                                )
                                            }
                                    }
                                    .background(
                                        Color(0xffA47449),
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
                                    text = viewModel.startingKeyboard[i].toString(),
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
                        }
                        .fillMaxWidth()
                        .padding(start = AppTheme.dimens.medium2, end = AppTheme.dimens.medium2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    for (i in 5..8) {
                        if (liveInput?.contains(viewModel.startingKeyboard[i])
                                ?.not() == true && viewModel.guessedChars.contains(viewModel.startingKeyboard[i])
                                .not()
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(AppTheme.dimens.medium2)
                                    .clickable {
                                        focusArray
                                            ?.indexOf(0)
                                            ?.let {
                                                viewModel.updateInput(
                                                    it,
                                                    viewModel.startingKeyboard[i]
                                                )
                                            }
                                    }
                                    .background(
                                        Color(0xffA47449),
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
                                    text = viewModel.startingKeyboard[i].toString(),
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
                                    ))
                        }

                    }
                }

            }
        }
    }
}