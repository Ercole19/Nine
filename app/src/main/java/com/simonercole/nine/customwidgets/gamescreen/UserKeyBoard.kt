package com.simonercole.nine.customwidgets.gamescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.simonercole.nine.R
import com.simonercole.nine.model.KeyboardTile
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.difficultyDialogBackground
import com.simonercole.nine.theme.fullInputTile
import com.simonercole.nine.utils.ConstraintLayoutMargins
import com.simonercole.nine.viewmodel.NineGameViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserKeyBoard(modifier: Modifier, viewModel : NineGameViewModel, currentKeyBoard : SnapshotStateList<KeyboardTile>) {
    Box(modifier
        .fillMaxWidth()
        .fillMaxHeight(0.3f)
    ) {
        ConstraintLayout {
            val (inputTop, inputBottom, confirmBtn) = createRefs()

            if (viewModel.isInputFull()) {
                Card(onClick = {
                    viewModel.makeGuess()
                },
                    modifier = Modifier
                        .constrainAs(confirmBtn) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                            end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(AppTheme.dimens.logoSize, AppTheme.dimens.medium3),
                    backgroundColor = difficultyDialogBackground,
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
                            text = stringResource(id = R.string.Confirm),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .constrainAs(inputTop) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                        end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin1)
                        bottom.linkTo(inputBottom.top, ConstraintLayoutMargins.smallMargin2)
                    }
                    .fillMaxWidth()
                    .padding(start = AppTheme.dimens.small1, end = AppTheme.dimens.small2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                for (i in 0..4) {
                    if (currentKeyBoard[i].isVisible && !currentKeyBoard[i].isGuessed) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.dimens.medium2)
                                .clickable {
                                    viewModel.updateInput(
                                        viewModel.getCurrentFocus(),
                                        currentKeyBoard[i].value
                                    )
                                }
                                .background(
                                    fullInputTile,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )
                                .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                .border(
                                    2.dp,
                                    Color.Black,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                ),
                            contentAlignment = Alignment.Center,
                        )
                        {
                            Text(
                                text = currentKeyBoard[i].value.toString(),
                                style = AppTheme.typography.h6,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .size(AppTheme.dimens.medium2)
                                .background(
                                    Color.Transparent,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )
                                .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                .border(
                                    2.dp,
                                    Color.Transparent,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .constrainAs(inputBottom) {
                        top.linkTo(inputTop.bottom, ConstraintLayoutMargins.smallMargin1)
                        start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin3)
                        end.linkTo(parent.end, ConstraintLayoutMargins.smallMargin2)
                        bottom.linkTo(parent.bottom, ConstraintLayoutMargins.smallMargin3)
                    }
                    .fillMaxWidth()
                    .padding(start = AppTheme.dimens.medium2, end = AppTheme.dimens.medium2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                for (i in 5..8) {
                    if (currentKeyBoard[i].isVisible && !currentKeyBoard[i].isGuessed
                    ) {

                        Box(
                            modifier = Modifier
                                .size(AppTheme.dimens.medium2)
                                .clickable {
                                    viewModel.updateInput(
                                        viewModel.getCurrentFocus(),
                                        currentKeyBoard[i].value
                                    )
                                }
                                .background(
                                    fullInputTile,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )
                                .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                .border(
                                    2.dp,
                                    Color.Black,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                ),
                            contentAlignment = Alignment.Center,
                        )
                        {
                            Text(
                                text = currentKeyBoard[i].value.toString(),
                                style = AppTheme.typography.h6,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .size(AppTheme.dimens.medium2)
                                .background(
                                    Color.Transparent,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )
                                .clip(shape = RoundedCornerShape(AppTheme.dimens.small1))
                                .border(
                                    2.dp,
                                    Color.Transparent,
                                    shape = RoundedCornerShape(AppTheme.dimens.small1)
                                )) } }
            }

        }
    }
}