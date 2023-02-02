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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.qamar.elasticview.ElasticView
import com.selsela.takeefapp.ui.auth.AuthUiState
import com.selsela.takeefapp.ui.common.Countdown
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.ElasticLoadingButton
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.ui.common.components.CardFace
import com.selsela.takeefapp.ui.common.components.FlipButton
import com.selsela.takeefapp.ui.home.OrderUiState
import com.selsela.takeefapp.ui.home.OrderViewModel
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.getCurrency
import com.selsela.takeefapp.utils.Extensions.Companion.log
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.delay

@Composable
fun AddCostScreen(
    vm: OrderViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val viewState: OrderUiState by vm.uiState.collectAsStateLifecycleAware(OrderUiState())
    val context = LocalContext.current

    AddCostContent(
        uiState = viewState,
        vm
    ) {
        vm.addAdditionalCost(293)
    }


    /**
     * Handle Ui state from flow
     */

    viewState.onSuccess?.let {
        EventEffect(
            event = it,
            onConsumed = vm::onSuccess
        ) {
            onFinish()
        }
    }

    EventEffect(
        event = viewState.onFailure,
        onConsumed = vm::onFailure
    ) { error ->
        Common.handleErrors(
            error.responseMessage,
            error.errors,
            context
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun AddCostContent(
    uiState: OrderUiState,
    vm: OrderViewModel, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        var state by remember {
            mutableStateOf(CardFace.Front)

        }
        if (uiState.state == State.LOADING)
            state.next
        else state = CardFace.Front
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
                    text = stringResource(id = R.string.cost_added),
                    style = text16Medium,
                    color = Color.White
                )

                Column(
                    Modifier
                        .paddingTop(28)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.required_cost),
                        style = text11,
                        color = SecondaryColor.copy(0.85f),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    EditText(
                        onValueChange = {
                            vm.additionalCost = it
                            if (!vm.isValid.value){
                                vm.isValid.value = true
                                vm.errorMessage.value = ""
                            }
                        }, text = vm.additionalCost,
                        hint = "00.00",
                        inputType = KeyboardType.Decimal,
                        trailing = {
                            Text(
                                text = stringResource(id = R.string.currency_1, getCurrency()),
                                style = text14,
                                color = SecondaryColor
                            )
                        },
                        isValid = vm.isValid.value,
                        validationMessage = vm.errorMessage.value,
                        borderColor = vm.validateBorderColor()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Countdown(
                            seconds = 120,
                            modifier = Modifier.paddingTop(12)
                        ) {}
                    }

                }
            }


            FlipButton(cardFace = state,
                back = {
                    ElasticLoadingButton(
                        onClick = {
                            state = state.next
                        },
                        title = stringResource(R.string.wait_for_payment),
                        colorBg = LightBlue.copy(0.10f),
                        modifier = Modifier
                            .paddingTop(22)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .requiredHeight(48.dp),
                        textColor = LightBlue
                    )
                },
                front = {
                    ElasticButton(
                        onClick = {
                            if (vm.isValid.value) {
                                state = state.next
                            }
                            onClick()
                        }, title = stringResource(id = R.string.send_order),
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
