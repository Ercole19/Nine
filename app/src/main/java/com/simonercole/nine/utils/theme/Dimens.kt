package com.simonercole.nine.utils.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val extraSmall: Dp = 0.dp,
    val small1: Dp = 0.dp,
    val small2: Dp = 0.dp,
    val small3: Dp = 0.dp,
    val medium1: Dp = 0.dp,
    val medium2: Dp = 0.dp,
    val medium3: Dp = 0.dp,
    val large: Dp = 0.dp,
    val buttonHeight: Dp = 40.dp,
    val logoSize: Dp = 42.dp,
    val tileDimensions : Dp = 0.dp,
    val smallTileDimensions : Dp = 0.dp,
    )


val CompactSmallDimens = Dimens (
    small1 = 7.dp,
    small2 = 10.dp,
    small3 = 14.dp,
    medium1 = 24.dp,
    medium2 = 40.dp,
    medium3 = 60.dp,
    large = 80.dp,
    buttonHeight = 120.dp,
    logoSize = 187.dp,
    tileDimensions = 33.dp,
    smallTileDimensions = 27.dp,
)


val CompactMediumDimens = Dimens(
    small1 = 10.dp,
    small2 = 15.dp,
    small3 = 20.dp,
    medium1 = 30.dp,
    medium2 = 50.dp,
    medium3 = 70.dp,
    large = 100.dp,
    buttonHeight = 150.dp,
    logoSize = 250.dp,
    tileDimensions = 42.dp,
    smallTileDimensions = 36.dp
)
val CompactDimens = Dimens (
    small1 = 10.dp,
    small2 = 15.dp,
    small3 = 20.dp,
    medium1 = 30.dp,
    medium2 = 36.dp,
    medium3 = 40.dp,
    large = 85.dp,
    logoSize = 200.dp
)

val MediumDimens = Dimens(
    small1 = 10.dp,
    small2 = 15.dp,
    small3 = 20.dp,
    medium1 = 35.dp,
    medium2 = 40.dp,
    medium3 = 50.dp,
    large = 110.dp,
    logoSize = 300.dp
)

val ExpandedDimens = Dimens (
    small1 = 15.dp,
    small2 = 20.dp,
    small3 = 25.dp,
    medium1 = 30.dp,
    medium2 = 35.dp,
    medium3 = 40.dp,
    large = 150.dp,
    logoSize = 350.dp
)
