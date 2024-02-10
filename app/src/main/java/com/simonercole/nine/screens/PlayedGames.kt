package com.simonercole.nine.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.customwidgets.playedgamesscreen.PlayedGamesList
import com.simonercole.nine.customwidgets.playedgamesscreen.PlayedGamesMenu
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.viewmodel.DoneGamesFactory
import com.simonercole.nine.viewmodel.PlayedGamesViewModel
import com.simonercole.nine.theme.AppTheme
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
    val expanded by viewModel.observableExpandedMenu.observeAsState()
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
                    IconButton(onClick = { viewModel.changeExpandedValue() }) {
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
           PlayedGamesMenu(
               isExpanded = expanded!!,
               viewModel = viewModel,
               chosenDifficulty = chosenDifficulty!!,
               gameResult = gameResult!!,
               showBestTimes = showBestTimes!!
           )
        }

        ConstraintLayout {
            ConstraintLayoutMargins.SetConstraintMargins()
            val (playedGamesBox) = createRefs()
            PlayedGamesList(
                modifier = Modifier.constrainAs(playedGamesBox) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
                innerPadding = innerPadding ,
                playedGames = playedGames!!,
                viewModel = viewModel
            )
        }
    }

}
