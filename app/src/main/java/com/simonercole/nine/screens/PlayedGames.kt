package com.simonercole.nine.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.*
import me.saket.cascade.CascadeDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.viewmodel.DoneGamesFactory
import com.simonercole.nine.viewmodel.PlayedGamesViewModel
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.no_cell
import com.simonercole.nine.theme.ok_cell
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.simonercole.nine.utils.Routes

@SuppressLint("RememberReturnType")
@Composable
fun PlayedGames(navController: NavHostController) {
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val viewModel : PlayedGamesViewModel = viewModel(factory = DoneGamesFactory(context.applicationContext as Application))
    PlayedGames(viewModel, navController)
}

@SuppressLint("SuspiciousIndentation", "UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayedGames(
    viewModel: PlayedGamesViewModel,
    navController: NavHostController
) {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    var expanded by remember { mutableStateOf(false) }
    val chosenDifficulty by viewModel.observableDifficulty.observeAsState()
    val showBestTimes by viewModel.observableSortByBestTimeIsChosen.observeAsState()
    val gameResult by viewModel.observableGameResult.observeAsState()
    val playedGames by viewModel.observableList.observeAsState()

    BackHandler(enabled = true, onBack = { viewModel.navigateToMainScreen(navController) })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ConstraintLayout {
                        ConstraintLayoutMargins.SetConstraintMargins()
                        val (arrow, text) = createRefs()
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "info",
                            modifier = Modifier
                                .constrainAs(arrow) {
                                    start.linkTo(parent.start, 5.dp)
                                    top.linkTo(parent.top, ConstraintLayoutMargins.smallMargin1)
                                }
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                )
                                {
                                    navController.navigate(Routes.NINE_START)
                                }
                                .size(AppTheme.dimens.medium1),
                            tint = Color.Black
                        )
                        Spacer(Modifier.size(AppTheme.dimens.medium2))
                        Text(
                            text = stringResource(id = R.string.played_games),
                            color = Color.Black,
                            style = AppTheme.typography.h2,
                            modifier = Modifier
                                .constrainAs(text) {
                                    top.linkTo(parent.top, ConstraintLayoutMargins.smallMargin1/2)
                                    start.linkTo(arrow.end, ConstraintLayoutMargins.mediumMargin2)
                                    end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin2)
                                }
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = Color(0xffD8B589)
                ),
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More menu",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .wrapContentSize(Alignment.TopEnd),
        ) {
            CascadeDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    viewModel.applyFiltersToList()
                }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.DifficultyFilter), style = AppTheme.typography.body1) },
                    children = {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.easy_diff), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (chosenDifficulty!! == Difficulty.Easy)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenDiff(Difficulty.Easy) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.medium_diff), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (chosenDifficulty!! == Difficulty.Medium)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenDiff(Difficulty.Medium) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.hard_diff), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (chosenDifficulty!! == Difficulty.Hard)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenDiff(Difficulty.Hard) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.All), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (chosenDifficulty!! == Difficulty.All)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenDiff(Difficulty.All) }
                        )

                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.GameStatus), style = AppTheme.typography.body1) },
                    enabled = showBestTimes == false,
                    children = {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.Won), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (gameResult == GameResult.ONLY_WIN)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenGameResult(GameResult.ONLY_WIN) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.Lost), style = AppTheme.typography.body1) },

                            trailingIcon = {
                                if (gameResult == GameResult.ONLY_LOSE)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenGameResult(GameResult.ONLY_LOSE) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.All), style = AppTheme.typography.body1) },
                            trailingIcon = {
                                if (gameResult == GameResult.ALL)
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppTheme.dimens.medium1)
                                    )
                            },
                            onClick = { viewModel.setChosenGameResult(GameResult.ALL) }
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.ShowBestTime), style = AppTheme.typography.body1) },
                    trailingIcon = {
                        if (showBestTimes == true)
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimens.medium1)
                            )
                    },
                    onClick = {
                        viewModel.filterByBestTime()
                    })
            }

        }

        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)) {
            ConstraintLayoutMargins.SetConstraintMargins()
            val (box) = createRefs()

            Box(modifier = Modifier
                .constrainAs(box) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight()
                .fillMaxWidth()

            ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                    ) {
                        playedGames!!.forEach{ playedGame ->
                            val date = LocalDateTime.parse(playedGame.game.dateTime)
                            item {
                                if (playedGame.showElementByResult && playedGame.showElementByDifficulty && !playedGame.deleted) {
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 5.dp, horizontal = 5.dp)
                                            .fillMaxWidth()
                                            .background(Color(0xfff7efd2))
                                            .border(
                                                width = 2.dp,
                                                shape = RoundedCornerShape(5.dp),
                                                color = if (playedGame.game.win) ok_cell else no_cell
                                            ),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                        ) {
                                            Text(
                                                text = formatterDate.format(date),
                                                color = Color.Black
                                            )
                                            Text(
                                                text = formatterTime.format(date),
                                                color = Color.Black
                                            )

                                        }
                                        Column(
                                            modifier = Modifier
                                                .padding(AppTheme.dimens.small1)
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.GameDuration)+ " " + playedGame.game.time,
                                                style = AppTheme.typography.body1,
                                                color = Color.Black,
                                                modifier = Modifier
                                            )
                                            Text(
                                                text = stringResource(id = R.string.attemptsPlayedGames) + " " + playedGame.game.attempts,
                                                style = AppTheme.typography.body1,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = stringResource(id = R.string.Difficulty) + " " +
                                                        when (playedGame.game.difficulty ){
                                                            Difficulty.Easy -> stringResource(id =R.string.easy_diff)
                                                            Difficulty.Medium -> stringResource(id = R.string.medium_diff)
                                                            else -> stringResource(id =R.string.hard_diff)
                                                        }
                                                ,
                                                style = AppTheme.typography.body1,
                                                color = Color.Black
                                            )

                                        }
                                        Column(Modifier.padding(AppTheme.dimens.small1)) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.removeGame(playedGame.game)
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
            }
        }
    }

}
