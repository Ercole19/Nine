package com.simonercole.nine.customwidgets.playedgamesscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.simonercole.nine.R
import com.simonercole.nine.model.PlayedGame
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.no_cell
import com.simonercole.nine.theme.ok_cell
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.PlayedGamesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PlayedGamesList(modifier : Modifier, innerPadding : PaddingValues, playedGames : SnapshotStateList<PlayedGame>, viewModel : PlayedGamesViewModel) {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    ConstraintLayout(
        modifier
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
                playedGames.forEach{ playedGame ->
                    val date = LocalDateTime.parse(playedGame.game.dateTime)
                    item {
                        if (playedGame.showElementByResult && playedGame.showElementByDifficulty && !playedGame.deleted) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = AppTheme.dimens.small1/2, horizontal = AppTheme.dimens.small1/2)
                                    .fillMaxWidth()
                                    .background(Color(0xfff7efd2))
                                    .border(
                                        width = 2.dp,
                                        shape = RoundedCornerShape(AppTheme.dimens.small1),
                                        color = if (playedGame.game.win) ok_cell else no_cell
                                    ),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(AppTheme.dimens.small1/2)
                                ) {
                                    if (playedGame.record) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Record",
                                            modifier = Modifier.size(AppTheme.dimens.small2),
                                            tint = Color.Black
                                        )
                                    }
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
                                        text = stringResource(id = R.string.GameDuration) + " " + playedGame.game.time,
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
                                                    Difficulty.Easy -> stringResource(id = R.string.easy_diff)
                                                    Difficulty.Medium -> stringResource(id = R.string.medium_diff)
                                                    else -> stringResource(id = R.string.hard_diff)
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