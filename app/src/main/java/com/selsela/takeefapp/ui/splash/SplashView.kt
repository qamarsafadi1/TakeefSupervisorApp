package com.selsela.takeefapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.AppLogoImage
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.sloganStyle
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text17
import com.selsela.takeefapp.ui.theme.textMeduim
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    onFinish: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashart),
            contentDescription = ""
        )

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            AppLogoImage()
            Text(
                text = "مشرف صيانة",
                style = text17,
                modifier = Modifier.paddingTop(30)
            )
        }
        LaunchedEffect(Unit) {
            delay(3000)
            onFinish()
        }
    }
}

@Composable
fun ConditionAnimation() {
    LottieAnimationView(
        modifier = Modifier
            .requiredHeight(184.dp)
            .padding(top = 42.dp),
        raw = R.raw.splashanimation
    )
}

@Composable
fun Color.ChangeStatusBarColor(
    isDark: Boolean = false
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = this,
        darkIcons = false
    )
    systemUiController.setNavigationBarColor(
        color = this,
        darkIcons = true
    )
}

@Composable
fun Color.ChangeNavigationBarColor() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(
        color = this,
        darkIcons = false
    )
}

@Composable
fun Color.ChangeStatusBarOnlyColor(
    isDark: Boolean = false
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = this,
        darkIcons = false
    )
}