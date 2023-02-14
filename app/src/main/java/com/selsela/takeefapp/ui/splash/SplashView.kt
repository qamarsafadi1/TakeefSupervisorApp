package com.selsela.takeefapp.ui.splash

import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    viewModel: ConfigViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor(true)

    SplashContent(onFinish)
    LaunchedEffect(Unit) {
        /**
         * Get fcm token
         */
       // viewModel.getConfig()
        receiveToken()
    }
}

@Composable
private fun SplashContent(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashart),
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillBounds
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
                text = stringResource(id = R.string.app_name),
                style = text17,
                modifier = Modifier.paddingTop(30)
            )
        }
        LaunchedEffect(Unit) {
            delay(7000)
            onFinish()
        }
    }
}

@Composable
fun Color.ChangeStatusBarColor(
    isDark: Boolean = false
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = this,
        darkIcons = isDark
    )
    systemUiController.setNavigationBarColor(
        color = this,
        darkIcons = isDark
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

 fun receiveToken() {
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token =>", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            token.log("Token")
            LocalData.fcmToken = token
        })
}