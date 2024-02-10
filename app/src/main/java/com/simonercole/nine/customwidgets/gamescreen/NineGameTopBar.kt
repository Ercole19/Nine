package com.simonercole.nine.customwidgets.gamescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun GameTopAppBar(viewModel  :NineGameViewModel, gameStatus : GameStatus, navController :NavHostController, userAttempts  :Int, timerValue : MutableState<Int>, modifier: Modifier) {
    ConstraintLayout(modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        ConstraintLayoutMargins.SetConstraintMargins()
        val (distanceRow, title, refreshButton, timer, backIcon, attempts, ) = createRefs()
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "arrow back",
            modifier = Modifier.constrainAs(backIcon) {
                top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin1)
                start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
            }

                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                )
                {
                    if (gameStatus == GameStatus.OnGoing) viewModel.quitRequest()
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
                    if (gameStatus == GameStatus.OnGoing) viewModel.refreshRequest()
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
                    top.linkTo(parent.top, ConstraintLayoutMargins.mediumMargin1)
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
            text = viewModel.getTimerLabel(timerValue.value),
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
                }) {}
    }
}
