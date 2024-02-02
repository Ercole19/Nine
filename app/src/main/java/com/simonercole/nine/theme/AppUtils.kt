package com.simonercole.nine.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

@Composable
fun ProvideAppUtils(
    appDimens: Dimens,
    typography: Typography,
    content: @Composable () -> Unit,
) {
    val appDimensions = remember { appDimens }
    val appTypography = remember { typography }
    CompositionLocalProvider(LocalAppDimens provides appDimensions, LocalAppTypo provides appTypography) {
        content()
    }
}

val LocalAppTypo = compositionLocalOf { typographyCompact }

val LocalAppDimens = compositionLocalOf {
    CompactDimens
}