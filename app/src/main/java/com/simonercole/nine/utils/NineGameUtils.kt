package com.simonercole.nine.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.simonercole.nine.R
import java.io.File
import java.io.Serializable

class NineGameUtils {
    companion object {
        const val symbols = "ABCDEFGHILMNOPQRSTUVZXYJK1234567890" //\u2600\u2601\u2602\u2603\u2604\u2605\u2606\u2607\u2608\u2609\u260A\u260B\u260C\u260D\u260E\u260F\u2610\u2611\u2612\u2613\u2614\u2615\u2616\u2617\u2618\u2619\u261A\u261B\u261C\u261D\u261E\u261F\u2620\u2621\u2622\u2623\u2624\u2625\u2626\u2627\u2628\u2629\u262A\u262B\u262C\u262D\u262E\u262F\u2630\u2631\u2632\u2633\u2634\u2635\u2636\u2637\u2638\u2639\u263A\u263B\u263C\u263D\u263E\u263F\u2640\u2641\u2642\u2643\u2644\u2645\u2646\u2647\u2648\u2649\u264A\u264B\u264C\u264D\u264E\u264F\u2650\u2651\u2652\u2653\u2654\u2655\u2656\u2657\u2658\u2659\u265A\u265B\u265C\u265D\u265E\u265F"

        fun getAttempts(difficulty: String, easy : String, medium : String) : Int {
            return when(difficulty) {
                easy -> 4
                medium -> 4
                else -> 3
            }
        }

        fun getTime(difficulty: String, easy: String, medium: String) : String {
            return when(difficulty) {
                easy -> "01:40"
                medium -> "01:20"
                else -> "00:50"
            }
        }
        fun getTimerLabel(value: Int): String {
            return "${padding(value / 60)} : ${padding(value % 60)}"
        }

        private fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

        fun parseIt(time: String): Int {
            val firstOne = time.substring(0, 2)
            val secondOne = time.substring(5, 7)
            val result = firstOne + secondOne
            return result.toInt()
        }
    }
}