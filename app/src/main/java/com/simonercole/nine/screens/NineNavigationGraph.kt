package com.simonercole.nine.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simonercole.nine.screens.start_screen.NineStart
import com.simonercole.nine.utils.Routes

@Composable
fun NineNavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.NINE_START) {

        composable(Routes.NINE_START) {
            NineStart(navController)
        }

        composable(
            "${Routes.SECOND_SCREEN}/{difficulty}",
            arguments = listOf(
                navArgument(name = "difficulty") {
                    type = NavType.StringType
                },
            )
        ) {
            val difficulty = it.arguments?.getString("difficulty")
            if (difficulty != null) {
                SecondScreen(difficulty.toString(), navController)
            }
        }

        composable(Routes.PLAYED_GAMES) {
            PlayedGames(navController)
        }

    }

}


