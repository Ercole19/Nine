package com.simonercole.nine.customwidgets.gamescreen

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.viewmodel.NineGameViewModel

@Composable
fun PauseGameDialog(viewModel : NineGameViewModel, navController : NavHostController) {
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
}