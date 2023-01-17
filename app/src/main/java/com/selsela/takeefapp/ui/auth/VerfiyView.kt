package com.selsela.takeefapp.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.TextFieldBg
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text18
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.selsela.takeefapp.ui.common.Countdown
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.OtpTextField
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop

@Composable
fun VerifyView(
    goToAddress: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TextColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(33.dp),
                backgroundColor = TextFieldBg,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .padding(top = 0.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 350.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 73.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = stringResource(R.string.verify_code),
                        style = text18,
                        color = Color.White
                    )
                    Text(
                        text = "الرجاء ادخال رقم الجوال المرسل الى رقم الجوال",
                        style = text11,
                        modifier = Modifier.paddingTop(20)
                    )
                    Text(
                        text = "966591234567",
                        style = text18,
                        color = Purple40,
                        modifier = Modifier.paddingTop(17)

                    )
                    var otpValue by remember {
                        mutableStateOf("")
                    }

                    OtpTextField(
                        otpText = otpValue,
                        onOtpTextChange = { value, otpInputFilled ->
                            otpValue = value
                        },
                        modifier = Modifier.paddingTop(25),
                    )

                    Text(
                        text = stringResource(R.string.you_will_recive_code_in),
                        style = text12,
                        color = Color.White,
                        modifier = Modifier.paddingTop(36)
                    )
                    Countdown(
                        30,
                        modifier = Modifier.paddingTop(8)
                    )
                }

            }

            ElasticButton(
                onClick = {
                    goToAddress()
                }, title = stringResource(R.string.confirm),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .requiredHeight(48.dp)
            )
        }

        LottieAnimationView(
            raw = R.raw.waiting,
            modifier = Modifier
                .size(
                    width = 250.dp,
                    height = 250.dp
                )
                .align(Alignment.TopCenter)
                .padding(bottom = 35.dp)
        )
        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 43.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logosmallwhite),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(19.5.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .requiredHeight(51.dp)
                    .background(LightBlue.copy(0.07f), shape = RoundedCornerShape(25.dp))
            ) {
                Text(text = stringResource(R.string.facing_problem), style = text12,
                color = Color.White.copy(0.85f))
                Text(
                    text = stringResource(R.string.support_lbl),
                    style = text12Meduim,
                    color = LightBlue,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}