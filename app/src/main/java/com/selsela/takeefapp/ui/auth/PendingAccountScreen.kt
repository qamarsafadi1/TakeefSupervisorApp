package com.selsela.takeefapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.auth.component.SupportBottomSection
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.theme.CircleColor
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text16
import com.selsela.takeefapp.ui.theme.text16Line
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.ui.theme.text18
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.delay

@Preview
@Composable
fun PendingAccountScreen(
    onFinish: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CircleColor)
                    .size(304.dp),
                contentAlignment = Alignment.Center
            ) {

                LottieAnimationView(
                    raw = R.raw.man,
                    modifier = Modifier.size(281.dp)
                )
                LottieAnimationView(
                    raw = R.raw.waitingicon,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.TopStart)
                        .size(115.dp)
                )

            }
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                text = stringResource(R.string.welcome_1),
                style = text16,
                color = TextColor
            )
            Text(
                text = "احمد محمد علي",
                style = text16Medium,
                color = Purple40,
                modifier = Modifier.paddingTop(9)
            )
            Text(
                text = stringResource(R.string.under_review),
                style = text18,
                color = TextColor,
                modifier = Modifier.paddingTop(23)
            )
            Text(
                text = stringResource(R.string.under_review_lbl) +
                        stringResource(R.string.under_review_lbl_1),
                style = text16Line,
                color = TextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingTop(23),
                textAlign = TextAlign.Center
            )

        }
        SupportBottomSection(
            Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 43.dp)
        ){

        }
    }
}