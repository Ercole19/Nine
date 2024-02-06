package com.simonercole.nine.screens.start_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.utils.NineGameUtils
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.difficultyDialogBackground
import com.simonercole.nine.theme.iconBtnColor
import com.simonercole.nine.viewmodel.FirstScreenViewModel

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DifficultyDialog( navHostController: NavHostController, viewModel : FirstScreenViewModel) {
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val openDialog = viewModel.observableOpenDialog.observeAsState()
    val difficultyChosen = viewModel.observableDifficultyChosen.observeAsState()
    val confirmVisibility = viewModel.observableConfirmVisibility.observeAsState()

    val explanation1 = stringResource(id = R.string.Explanation_1)
    val explanation2 = stringResource(id = R.string.Explanation_2)
    val easyString = stringResource(id = R.string.easy_diff)
    val mediumString = stringResource(id = R.string.medium_diff)
    val hardString = stringResource(id = R.string.hard_diff)


    if (difficultyChosen.value == true) {
        viewModel.changeVisibility()
    }


    if (openDialog.value == true) {
        AlertDialog(
            backgroundColor = difficultyDialogBackground,
            onDismissRequest = {
                viewModel.handleDialogClosing()
            },
            title = {
                Text(
                    text = stringResource(id = R.string.Choose_diff),
                    style = AppTheme.typography.h4,
                    color = Color.Black
                )
            },
            text = {
                val radioOptions = listOf(easyString, mediumString, hardString)
                val (selectedOption, onOptionSelected) = remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                        radioOptions.forEach { difficulty ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = AppTheme.dimens.small1),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (difficulty == selectedOption),
                                    onClick = {
                                        viewModel.changeDifficulty(difficulty, easyString, mediumString)
                                        onOptionSelected(difficulty)
                                    }
                                )
                                Text(
                                    text = difficulty,
                                    style = AppTheme.typography.body2,
                                )
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Info icon",
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                        )
                                        {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "$explanation1   " + NineGameUtils
                                                        .getAttempts(
                                                            difficulty,
                                                            easyString,
                                                            mediumString
                                                        )
                                                        .toString() + "  " + explanation2 + "  " +
                                                            NineGameUtils.getTime(
                                                                difficulty, easyString, mediumString
                                                            ),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                        .padding(
                                            start = AppTheme.dimens.medium1
                                        ),
                                    tint = iconBtnColor
                                )
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleDialogClosing()
                        viewModel.navigateGameScreen(navHostController)
                    },
                    enabled = difficultyChosen.value!!,
                    modifier = Modifier.alpha(confirmVisibility.value!!)
                ) {
                    Text(stringResource(id = R.string.play), style = AppTheme.typography.h6)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.handleDialogClosing()
                }) {
                    Text(stringResource(id = R.string.back), style = AppTheme.typography.h6)
                }
            }
        )
    }
}