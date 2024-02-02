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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.utils.NineGameUtils
import com.simonercole.nine.utils.Routes
import com.simonercole.nine.utils.theme.AppTheme
import com.simonercole.nine.utils.NineGameUtils.Difficulty
import com.simonercole.nine.viewmodel.FirstScreenViewModel

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DifficultyDialog( navHostController: NavHostController, viewModel : FirstScreenViewModel) {
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val openDialog = viewModel.openDialog.observeAsState()
    var gameDifficulty = viewModel.gameDifficulty.observeAsState()
    val difficultyChosen = viewModel.difficultyChosen.observeAsState()
    val confirmVisibility = viewModel.confirmVisibility.observeAsState()

    val explanation1 = stringResource(id = R.string.Explanation_1)
    val explanation2 = stringResource(id = R.string.Explanation_2)
    val easyString = stringResource(id = R.string.easy_diff)
    val mediumString = stringResource(id = R.string.medium_diff)


    if (difficultyChosen.value!!.value) {
       viewModel.changeVisibility()
    }


    if (openDialog.value!!) {
        AlertDialog(
            backgroundColor = Color(0xfff8f1e7 ),
            onDismissRequest = {
                viewModel.resetValues()
            },
            title = {
                androidx.compose.material.Text(
                    text = stringResource(id = R.string.Choose_diff),
                    style = AppTheme.typography.h4,
                    color = Color.Black
                )
            },
            text = {
                val radioOptions = listOf(stringResource(id = R.string.easy_diff), stringResource(id = R.string.medium_diff), stringResource(id = R.string.hard_diff))
                val (selectedOption, onOptionSelected) = remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .padding(start = AppTheme.dimens.small3, top = AppTheme.dimens.medium1),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(Modifier.fillMaxHeight()) {
                        radioOptions.forEach { difficulty ->
                            Spacer(modifier = Modifier.size(AppTheme.dimens.small2))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppTheme.dimens.small2),
                            ) {
                                RadioButton(
                                    selected = (difficulty == selectedOption),
                                    onClick = {
                                        viewModel.changeDifficulty(difficulty, easyString, mediumString )
                                        onOptionSelected(difficulty)
                                    }
                                )
                                Text(
                                    text = difficulty,
                                    style = AppTheme.typography.body2,
                                    modifier = Modifier.padding(
                                        top = AppTheme.dimens.small2,
                                        start = AppTheme.dimens.small2
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "info",
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
                                                        .getAttempts(difficulty, easyString, mediumString)
                                                        .toString() + "  " + explanation2 + "  " +
                                                            NineGameUtils.getTime(
                                                                difficulty, easyString, mediumString
                                                            ),
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                        .padding(
                                            top = AppTheme.dimens.small2,
                                            start = AppTheme.dimens.medium1
                                        ),
                                    tint = Color(0xff2d8bba)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetValues()
                        navHostController.navigate(Routes.SECOND_SCREEN + "/${gameDifficulty.value}")
                    },
                    enabled = difficultyChosen.value!!.value,
                    modifier = Modifier.alpha(confirmVisibility.value!!.floatValue)
                ) {
                    Text(stringResource(id = R.string.play), style = AppTheme.typography.h6)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetValues()
                }) {
                    Text(stringResource(id = R.string.back), style = AppTheme.typography.h6)
                }
            }
        )
    }
}




