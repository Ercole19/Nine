package com.simonercole.nine.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.simonercole.nine.MainActivity


/**
 * Composable function that applies the theme for the Nine Game application.
 * @param activity The current activity.
 * @param content The composable content to be wrapped.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NineTheme(activity: Activity = LocalContext.current as MainActivity, content: @Composable () -> Unit) {
    // Calculate window size class
    val window = calculateWindowSizeClass(activity = activity)
    val config = LocalConfiguration.current

    // Initialize typography and dimension values
    val typography: Typography
    val appDimens: Dimens

    // Determine typography and dimension values based on window size class
    when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            if (config.screenWidthDp <= 360) {
                appDimens = CompactSmallDimens
                typography = typographySmallCompact
            } else if (config.screenWidthDp < 599) {
                appDimens = CompactMediumDimens
                typography = typographyMediumCompact
            } else {
                appDimens = CompactDimens
                typography = typographyCompact
            }
        }
        WindowWidthSizeClass.Medium -> {
            appDimens = MediumDimens
            typography = typographyMedium
        }
        WindowWidthSizeClass.Expanded -> {
            appDimens = ExpandedDimens
            typography = typographyBig
        }
        else -> {
            appDimens = ExpandedDimens
            typography = typographyBig
        }
    }

    // Provide typography and dimensions to children using ProvideAppUtils
    ProvideAppUtils(appDimens = appDimens, typography = typography ) {
        // Apply MaterialTheme with provided typography and shapes
        MaterialTheme(
            typography = typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Object providing access to theme dimensions and typography.
 */
object AppTheme {
    /**
     * Retrieves the dimension settings for the app.
     */
    val dimens: Dimens
        @Composable
        get() = LocalAppDimens.current

    /**
     * Retrieves the typography settings for the app.
     */
    val typography: Typography
        @Composable
        get() = LocalAppTypo.current
}

