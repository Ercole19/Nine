package com.simonercole.nine.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

/**
 * Composable function that provides application utilities such as dimensions and typography to its children.
 * @param appDimens The dimensions for the application.
 * @param typography The typography settings for the application.
 * @param content The composable content to be wrapped.
 */
@Composable
fun ProvideAppUtils(
    appDimens: Dimens,
    typography: Typography,
    content: @Composable () -> Unit,
) {
    // Remember the provided dimensions and typography
    val appDimensions = remember { appDimens }
    val appTypography = remember { typography }

    // Provide the dimensions and typography via CompositionLocalProvider
    CompositionLocalProvider(LocalAppDimens provides appDimensions, LocalAppTypo provides appTypography) {
        content()
    }
}

// CompositionLocal for providing typography settings
val LocalAppTypo = compositionLocalOf { typographyCompact }

// CompositionLocal for providing dimensions
val LocalAppDimens = compositionLocalOf {
    CompactDimens
}
