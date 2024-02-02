package com.simonercole.nine.utils.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.simonercole.nine.MainActivity

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NineTheme(activity: Activity = LocalContext.current as MainActivity, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val window = calculateWindowSizeClass(activity = activity)
    val config = LocalConfiguration.current

    var typography : Typography
    var appDimens : Dimens


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

    ProvideAppUtils(appDimens = appDimens, typography = typography ) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = Shapes,
            content = content
        )
        
    }
}

object AppTheme{
    val dimens : Dimens
        @Composable
        get() = LocalAppDimens.current
    val typography : Typography
        @Composable
        get() = LocalAppTypo.current
}
