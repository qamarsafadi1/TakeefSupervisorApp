package com.selsela.takeefapp.ui.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.EditText
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import androidx.compose.runtime.*
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.input.KeyboardType
import com.qamar.elasticview.ElasticView
import com.selsela.takeefapp.ui.common.Countdown
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.ElasticLoadingButton
import com.selsela.takeefapp.ui.common.components.CardFace
import com.selsela.takeefapp.ui.common.components.FlipButton
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.text14
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddCostScreen(onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        var isSend by remember {
            mutableStateOf(false)
        }
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.artbg),
                    contentDescription = ""
                )
                LottieAnimationView(
                    raw = R.raw.man,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .align(Alignment.BottomCenter)
                        .size(281.dp)
                )
            }

            Column(
                Modifier
                    .paddingTop(23)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .requiredHeight(263.dp)
                    .background(TextColor, shape = RoundedCornerShape(33.dp))
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "التكلفة الاضافية للصيانة",
                    style = text16Medium,
                    color = Color.White
                )

                Column(
                    Modifier
                        .paddingTop(28)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "المبلغ المطلوب",
                        style = text11,
                        color = SecondaryColor.copy(0.85f),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    var cost by remember {
                        mutableStateOf("")
                    }
                    EditText(onValueChange = {
                        cost = it
                    }, text = cost,
                        hint = "00.00",
                        inputType = KeyboardType.Decimal,
                        trailing = {
                            Text(
                                text = "رس",
                                style = text14,
                                color = SecondaryColor
                            )
                        })
                    AnimatedVisibility(visible = isSend) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Countdown(
                                seconds = 120,
                                modifier = Modifier.paddingTop(12)
                            )

                            ElasticView(onClick = { /*TODO*/ }) {
                                Text(
                                    text = "اعادة ارسال",
                                    style = text14,
                                    color = SecondaryColor,
                                    modifier = Modifier.paddingTop(12)
                                )
                            }
                        }
                    }


                }

            }
            var state by remember {
                mutableStateOf(CardFace.Front)
            }
            FlipButton(cardFace = state,
                back = {
                    ElasticLoadingButton(
                        onClick = {
                            state = state.next
                        },
                        title = "انتظار الدفع",
                        colorBg = LightBlue.copy(0.10f),
                        modifier = Modifier
                            .paddingTop(22)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .requiredHeight(48.dp),
                        textColor = LightBlue
                    )

                    LaunchedEffect(Unit) {
                        delay(3000)
                        onFinish()
                    }

                },
                front = {
                    ElasticButton(
                        onClick = {
                            state = state.next
                            isSend = true
                        }, title = "إرسال الطلب",
                        modifier = Modifier
                            .paddingTop(22)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .requiredHeight(48.dp)
                    )
                }

            )
        }

    }
}
