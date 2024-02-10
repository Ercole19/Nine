package com.simonercole.nine.customwidgets.playedgamesscreen

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.simonercole.nine.R
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
import com.simonercole.nine.viewmodel.PlayedGamesViewModel
import me.saket.cascade.CascadeDropdownMenu

@Composable
fun PlayedGamesMenu(isExpanded : Boolean, viewModel: PlayedGamesViewModel) {
    val chosenDifficulty by viewModel.observableDifficulty.observeAsState()
    val showBestTimes by viewModel.observableSortByBestTimeIsChosen.observeAsState()
    val gameResult by viewModel.observableGameResult.observeAsState()

    CascadeDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            viewModel.changeExpandedValue()
            viewModel.applyFiltersToList()
        }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.DifficultyFilter), style = AppTheme.typography.body1) },
            children = {
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.easy_diff), style = AppTheme.typography.body1) },
                    trailingIcon = {
                        if (chosenDifficulty == Difficulty.Easy)
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimens.medium1)
                            )
                    },
                    onClick = { viewModel.setChosenDiff(Difficulty.Easy) }
                )
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.medium_diff), style = AppTheme.typography.body1) },
                    trailingIcon = {
                        if (chosenDifficulty == Difficulty.Medium)
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimens.medium1)
                            )
                    },
                    onClick = { viewModel.setChosenDiff(Difficulty.Medium) }
                )
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.hard_diff), style = AppTheme.typography.body1) },
                    trailingIcon = {
                        if (chosenDifficulty == Difficulty.Hard)
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimens.medium1)
                            )
                    },
                    onClick = { viewModel.setChosenDiff(Difficulty.Hard) }
                )
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.All), style = AppTheme.typography.body1) },
                    trailingIcon = {
                        if (chosenDifficulty == Difficulty.All)
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
            enabled = !showBestTimes!!,
            children = {
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
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
                androidx.compose.material3.DropdownMenuItem(
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
        androidx.compose.material3.DropdownMenuItem(
            text = { Text(stringResource(id = R.string.ShowBestTime), style = AppTheme.typography.body1) },
            trailingIcon = {
                if (showBestTimes!!)
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