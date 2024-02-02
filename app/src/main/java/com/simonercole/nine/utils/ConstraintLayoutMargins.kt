package com.simonercole.nine.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simonercole.nine.utils.theme.AppTheme

class ConstraintLayoutMargins {
    companion object {
        var largeMargin : Dp = 0.dp
        var mediumMargin3 : Dp = 0.dp
        var mediumMargin2 : Dp = 0.dp
        var mediumMargin1 : Dp = 0.dp
        var smallMargin3 : Dp = 0.dp
        var smallMargin2 : Dp = 0.dp
        var smallMargin1 : Dp = 0.dp
        var buttonHeight : Dp = 0.dp

        @Composable
        fun SetConstraintMargins() {
            largeMargin = AppTheme.dimens.large
            mediumMargin1 = AppTheme.dimens.medium1
            mediumMargin2 = AppTheme.dimens.medium2
            mediumMargin3 = AppTheme.dimens.medium3
            smallMargin1 = AppTheme.dimens.small1
            smallMargin2 = AppTheme.dimens.small2
            smallMargin3 = AppTheme.dimens.small3
            buttonHeight = AppTheme.dimens.buttonHeight
        }
    }
}