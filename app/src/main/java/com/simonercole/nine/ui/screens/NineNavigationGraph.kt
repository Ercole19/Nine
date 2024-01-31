package com.simonercole.nine.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simonercole.nine.ui.screens.nineStart.NineStart

@Composable
fun NineNavigationGraph() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.NINE_START) {

            composable(Routes.NINE_START ) {
                    NineStart(navController)
            }

            composable("${Routes.SECOND_SCREEN}/{difficulty}",
                arguments = listOf(navArgument(name = "difficulty") {type = NavType.StringType},
                )
            ) {
                val difficulty = it.arguments?.getString("difficulty")
                if (difficulty != null) {
                    SecondScreen(difficulty, navController)
                }
            }

            composable(Routes.PLAYED_GAMES) {
                PlayedGames(navController)
            }

        }
}