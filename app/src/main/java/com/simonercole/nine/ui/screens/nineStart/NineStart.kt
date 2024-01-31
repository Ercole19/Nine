package com.simonercole.nine.ui.screens.nineStart

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.simonercole.nine.R
import com.simonercole.nine.ui.model.Difficulty
import com.simonercole.nine.ui.screens.ConstraintLayoutMargins
import com.simonercole.nine.ui.screens.Routes
import com.simonercole.nine.ui.theme.AppTheme
import com.simonercole.nine.ui.theme.ScreenOrientation
import com.simonercole.nine.ui.theme.btnColor
import com.simonercole.nine.ui.theme.cells_background
import com.simonercole.nine.ui.theme.fontFamily
import com.simonercole.nine.ui.theme.fontFamily2
import com.simonercole.nine.ui.theme.fontFamily3
import com.simonercole.nine.ui.theme.startColor


@Composable
fun NineStart(navController: NavHostController) {
    val context = LocalContext.current
    BackHandler(enabled = true, onBack = {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, a, null)
    })

    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    NineStartPortrait(navController = navController)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NineStartPortrait(navController: NavHostController) {
    val showDifficulty = remember{mutableStateOf(false)}

    if (showDifficulty.value) {
        DifficultyDialog(showDifficulty, navController)
    }

    Box(modifier = Modifier
        .fillMaxHeight(0.8f)
        .fillMaxWidth()) {
        Image(
            painter = painterResource(id =  R.drawable.letters),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }


    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (box) = createRefs()
        ConstraintLayoutMargins.SetConstraintMargins()
        Box(modifier = Modifier
            .constrainAs(box) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .fillMaxWidth()
            .background(
                color = startColor,
                shape = RoundedCornerShape(topStart = AppTheme.dimens.medium1, topEnd = AppTheme.dimens.medium1)
            )
            .fillMaxHeight(0.4f)
            .clip(shape = RoundedCornerShape(topStart = AppTheme.dimens.medium1, topEnd = AppTheme.dimens.medium1))
        ) {
            ConstraintLayout(Modifier.fillMaxSize()) {
                val (text1, text2, bt1, bt2, bt3) = createRefs()

                Text(
                    text = "Nine",
                    style = AppTheme.typography.h1,
                    modifier = Modifier
                        .constrainAs(text1) {
                            top.linkTo(box.top)
                            start.linkTo(parent.start)

                        }
                        .padding(AppTheme.dimens.small3)

                )

                Text(
                        text = "A total new innovative word puzzle game",
                        style = AppTheme.typography.h4,
                        modifier = Modifier
                            .constrainAs(text2) {
                                top.linkTo(box.top, ConstraintLayoutMargins.mediumMargin2)
                                start.linkTo(parent.start)
                            }
                            .padding(AppTheme.dimens.small3)

                    )

                Card(onClick = { showDifficulty.value = true }, modifier = Modifier
                    .constrainAs(bt1) {
                        start.linkTo(parent.start, ConstraintLayoutMargins.smallMargin1)
                        end.linkTo(bt2.start, ConstraintLayoutMargins.smallMargin1)
                        top.linkTo(text2.bottom, ConstraintLayoutMargins.smallMargin2)
                    }
                    .size(AppTheme.dimens.buttonHeight, AppTheme.dimens.medium2),
                    backgroundColor = btnColor,
                    shape = RoundedCornerShape(AppTheme.dimens.small3),
                    elevation = AppTheme.dimens.small1
                )

                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Text(
                            style = AppTheme.typography.h6,
                            text = "Classic",
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Card(onClick = {
                    navController.navigate(Routes.PLAYED_GAMES)
                }, modifier = Modifier
                    .constrainAs(bt2) {
                        start.linkTo(bt1.end, margin = ConstraintLayoutMargins.smallMargin1)
                        end.linkTo(parent.end)
                        top.linkTo(text2.bottom, margin = ConstraintLayoutMargins.smallMargin2)
                    }
                    .size(AppTheme.dimens.buttonHeight, AppTheme.dimens.medium2),
                    backgroundColor = btnColor,
                    shape = RoundedCornerShape(AppTheme.dimens.small3),
                    elevation = AppTheme.dimens.small1
                )

                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Text(
                            style = AppTheme.typography.h6,
                            text = "Played games",
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }
    }
}