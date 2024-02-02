package com.simonercole.nine.screens.start_screen

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.utils.Routes
import com.simonercole.nine.utils.theme.AppTheme
import com.simonercole.nine.utils.theme.btnColor
import com.simonercole.nine.utils.theme.startColor
import com.simonercole.nine.viewmodel.DoneGamesFactory
import com.simonercole.nine.viewmodel.FirstScreenViewModel
import com.simonercole.nine.viewmodel.PlayedGamesViewModel


@Composable
fun NineStart(navController: NavHostController) {
    val context = LocalContext.current
    BackHandler(enabled = true, onBack = {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, a, null)
    })
    val viewModel : FirstScreenViewModel = viewModel(factory = FirstScreenViewModel.FirstScreenFactory(
        context.applicationContext as Application
    )
    )

    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    NineStartPortrait(navController = navController, viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NineStartPortrait(navController: NavHostController, viewModel: FirstScreenViewModel) {
    val showDifficulty by viewModel.showDifficulty.observeAsState()

    if (showDifficulty!!) {
        DifficultyDialog(navController, viewModel)
    }

    Box(modifier = Modifier
        .fillMaxHeight(0.8f)
        .fillMaxWidth()) {
        Image(
            painter = painterResource(id =  R.drawable.letters),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }


    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (box) = createRefs()
        ConstraintLayoutMargins.SetConstraintMargins()
        Box(modifier = Modifier
            .constrainAs(box) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .fillMaxWidth()
            .background(
                color = startColor,
                shape = RoundedCornerShape(
                    topStart = AppTheme.dimens.medium1,
                    topEnd = AppTheme.dimens.medium1
                )
            )
            .fillMaxHeight(0.4f)
            .clip(
                shape = RoundedCornerShape(
                    topStart = AppTheme.dimens.medium1,
                    topEnd = AppTheme.dimens.medium1
                )
            )
        ) {
            ConstraintLayout(Modifier.fillMaxSize()) {
                val (text1, text2, bt1, bt2) = createRefs()

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = AppTheme.typography.h1,
                    modifier = Modifier
                        .constrainAs(text1) {
                            top.linkTo(box.top)
                            start.linkTo(parent.start)

                        }

                        .padding(AppTheme.dimens.small3)

                )

                Text(
                        text = stringResource(id = R.string.text_intro),
                        style = AppTheme.typography.h4,
                        modifier = Modifier
                            .constrainAs(text2) {
                                top.linkTo(box.top, ConstraintLayoutMargins.mediumMargin2)
                                start.linkTo(parent.start)
                            }
                            .padding(AppTheme.dimens.small3)

                    )

                Card(onClick = { viewModel.setShowDifficulty() }, modifier = Modifier
                    .constrainAs(bt1) {
                        start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                        end.linkTo(bt2.start, ConstraintLayoutMargins.smallMargin1)
                        top.linkTo(text2.bottom, ConstraintLayoutMargins.mediumMargin2)
                    }
                    .size(AppTheme.dimens.buttonHeight, AppTheme.dimens.medium2),
                    backgroundColor = btnColor,
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
                            text = stringResource(id = R.string.play_button),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Card(onClick = {
                    navController.navigate(Routes.PLAYED_GAMES)
                }, modifier = Modifier
                    .constrainAs(bt2) {
                        start.linkTo(bt1.end, margin = ConstraintLayoutMargins.smallMargin1)
                        end.linkTo(parent.end)
                        top.linkTo(text2.bottom, margin = ConstraintLayoutMargins.mediumMargin2)
                    }
                    .size(AppTheme.dimens.buttonHeight, AppTheme.dimens.medium2),
                    backgroundColor = btnColor,
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
                            text = stringResource(id = R.string.played_games),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }
    }
}