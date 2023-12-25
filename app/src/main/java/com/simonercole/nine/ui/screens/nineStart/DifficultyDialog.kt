package com.simonercole.nine.ui.screens.nineStart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.simonercole.nine.ui.model.Difficulty
import com.simonercole.nine.ui.model.NineGameUtils
import com.simonercole.nine.ui.screens.Routes
import com.simonercole.nine.ui.theme.AppTheme

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DifficultyDialog(value: MutableState<Boolean>, navHostController: NavHostController) {
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    val openDialog = remember {mutableStateOf(value)}
    val gameDifficulty = remember { mutableStateOf(Difficulty.Easy.toString()) }
    val difficultyChosen = remember { mutableStateOf(false) }
    val confirmVisibility = remember { mutableFloatStateOf(0f) }

    if (difficultyChosen.value) {
        confirmVisibility.floatValue = 1f
    }


    if (openDialog.value.value) {
        AlertDialog(
            backgroundColor = Color(0xfff8f1e7 ),
            onDismissRequest = {
                openDialog.value.value = !openDialog.value.value
            },
            title = {
                androidx.compose.material.Text(
                    text = "Choose difficulty!",
                    style = AppTheme.typography.h4,
                    color = Color.Black
                )
            },
            text = {
                val radioOptions = listOf(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)
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
                                    selected = (difficulty.toString() == selectedOption),
                                    /*modifier = Modifier.padding(
                                        top = AppTheme.dimens.small2,
                                        start = AppTheme.dimens.small2
                                    ),*/
                                    onClick = {
                                        onOptionSelected(difficulty.toString())
                                        gameDifficulty.value = difficulty.toString()
                                        difficultyChosen.value = true
                                    }
                                )
                                Text(
                                    text = difficulty.toString(),
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
                                                    "You have " + NineGameUtils
                                                        .getAttempts(difficulty)
                                                        .toString() + " attempts to guess. Game duration : " + NineGameUtils.getTime(
                                                        difficulty
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
                        openDialog.value.value = !openDialog.value.value
                        navHostController.navigate(Routes.SECOND_SCREEN + "/${gameDifficulty.value}")
                    },
                    enabled = difficultyChosen.value,
                    modifier = Modifier.alpha(confirmVisibility.floatValue)
                ) {
                    Text("Play", style = AppTheme.typography.h6)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value.value = !openDialog.value.value }) {
                    Text("Back", style = AppTheme.typography.h6)
                }
            }
        )
    }
}






