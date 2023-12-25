package com.simonercole.nine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.simonercole.nine.ui.screens.NineNavigationGraph
import com.simonercole.nine.ui.screens.SecondScreen
import com.simonercole.nine.ui.theme.NineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NineTheme {
                NineApp()
            }
        }
    }
}

@Composable
fun NineApp() {
    NineNavigationGraph()
}




