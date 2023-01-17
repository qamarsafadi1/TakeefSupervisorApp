package com.selsela.takeefapp.ui.general

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.order.special.SuccessSend
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop

@Preview
@Composable
fun SuccessView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 21.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .animateContentSize(tween(500))
                .background(TextColor, RoundedCornerShape(33.dp))
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            SuccessSend()
        }

        LottieAnimationView(
            raw = R.raw.send,
            modifier = Modifier
                .paddingTop(112)
                .size(126.dp)
                .align(Alignment.TopCenter)
        )
    }
}