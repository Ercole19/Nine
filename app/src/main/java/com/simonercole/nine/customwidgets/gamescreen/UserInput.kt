package com.simonercole.nine.customwidgets.gamescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simonercole.nine.model.GameTile
import com.simonercole.nine.theme.AppTheme
import com.simonercole.nine.theme.fullInputTile
import com.simonercole.nine.viewmodel.NineGameViewModel

@Composable
fun UserInput(modifier: Modifier, currentInput : MutableList<GameTile>, viewModel : NineGameViewModel ){
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0..8) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimens.tileDimensions)
                    .border(
                        if (currentInput[i].isFocused && !currentInput[i].isGuessed) 2.dp else 1.dp,
                        color = if (currentInput[i].isFocused && !currentInput[i].isGuessed) Color.Red else Color.Black,
                    )
                    .background(
                        if (currentInput[i].value == ' ') Color.White else fullInputTile
                    )
                    .clickable {
                        if (!currentInput[i].isGuessed) {
                            viewModel.updateFocusByTouch(i)
                            if (currentInput[i].value != ' ') viewModel.deleteChar(i)
                        } else viewModel.updateFocusByTouch(i)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentInput[i].value.toString(),
                    style = AppTheme.typography.h6,
                    color = Color.White
                )
            }
        }

    }
}