package com.simonercole.nine.customwidgets.gamescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.distance_one
import com.simonercole.nine.theme.distance_three
import com.simonercole.nine.theme.distance_two
import kotlinx.coroutines.launch

@Composable
fun PastGuessesBox(modifier: Modifier, userInputs : SnapshotStateList<HashMap<Int, Pair<String, Char>>>) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyListState()
    Box(
        modifier
        .background(Color.White, RoundedCornerShape(AppTheme.dimens.small1))
        .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
        .border(2.dp, Color.Black, RoundedCornerShape(AppTheme.dimens.small1))
        .fillMaxHeight(0.3f)
        .fillMaxWidth(0.95f)
    ) {
        LazyColumn(modifier = Modifier.padding(AppTheme.dimens.small1), state = state) {

            coroutineScope.launch {
                if (userInputs.size > 2) {
                    state.animateScrollToItem(userInputs.size - 1)
                }
            }

            item {
                if (userInputs.isEmpty().not()) {
                    userInputs.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 0..8) {
                                Box(
                                    modifier = Modifier
                                        .size(AppTheme.dimens.smallTileDimensions)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    it[i]?.first.toString().let { it1 ->
                                        Text(
                                            text = it1,
                                            style = AppTheme.typography.h6,
                                            color =
                                            if (it[i]?.first.toString() == "?") Color.Black
                                            else {
                                                when (it[i]?.first.toString()) {
                                                    "0" -> Color.Green
                                                    "1" -> distance_one
                                                    "2" -> distance_two
                                                    "3" -> distance_three
                                                    else -> Color.Red
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 0..8) {
                                Box(
                                    modifier = Modifier
                                        .size(AppTheme.dimens.smallTileDimensions)
                                        .border(
                                            1.dp,
                                            color = Color.Black,
                                        )
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it[i]?.second.toString(),
                                        style = AppTheme.typography.h6,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(AppTheme.dimens.medium1))
                    }

                }
            }
        }
    }
}