package com.simonercole.nine.customwidgets.gamescreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel


@Composable
fun EndGameDialog(viewModel: NineGameViewModel, gameStatus: GameStatus?, newBestTime : Boolean?, navController : NavHostController) {
    AlertDialog(
        backgroundColor = Color(0xfffff8dc),
        onDismissRequest = {

        },
        title = {
            Text(
                text = if (gameStatus == GameStatus.Won) stringResource(id = R.string.WinningTitle) else stringResource(id = R.string.LosingTitle),
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
            if (gameStatus == GameStatus.Lost) {
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