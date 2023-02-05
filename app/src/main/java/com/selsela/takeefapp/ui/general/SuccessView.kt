package com.selsela.takeefapp.ui.general

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.ui.home.OrderUiState
import com.selsela.takeefapp.ui.home.OrderViewModel
import com.selsela.takeefapp.ui.order.rate.RateSheet
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text16Line
import com.selsela.takeefapp.ui.theme.text18
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.Extensions.Companion.showSuccess
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SuccessView(
    orderID: Int,
    viewModel: OrderViewModel = hiltViewModel(),
    goToRate: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor()
    val coroutineScope = rememberCoroutineScope()
    val viewState: OrderUiState by viewModel.uiState.collectAsStateLifecycleAware(
        OrderUiState()
    )
    viewState.log("viewState")
    viewState.order = Order(id = orderID)
    val rateSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(Modifier
            .align(Alignment.Center),
            contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .padding(bottom = 51.dp,top = 44.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .animateContentSize(tween(500))
                    .background(TextColor, RoundedCornerShape(33.dp))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                SuccessSend(){
                    coroutineScope.launch {
                        if (rateSheetState.isVisible)
                            rateSheetState.hide()
                        else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
            }
            LottieAnimationView(
                raw = R.raw.send,
                modifier = Modifier
                    .size(126.dp)
            )
        }

        RateSheet(rateSheetState,
            viewModel,
            viewState,
            onConfirm = viewModel::rateOrder)
    }

    when (viewState.state) {
        State.SUCCESS -> {
            if (viewState.responseMessage.isNullOrEmpty().not()) {
                LocalContext.current.showSuccess(
                    viewState.responseMessage ?: ""
                )
                LaunchedEffect(key1 = Unit){
                    coroutineScope.launch {
                        if (rateSheetState.isVisible)
                            rateSheetState.hide()
                        else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
                viewState.responseMessage = ""
                goToRate()
            }
        }
        else -> {}
    }
}


@Composable
fun SuccessSend(goToRate: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .requiredHeight(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(137.dp))

        Text(
            text = stringResource(R.string.payed_successfully), style = text18,
            color = Color.White
        )
        Text(
            text = stringResource(R.string.pay_lbl_1),
            style = text16Line,
            color = Color.White.copy(0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.paddingTop(21)
        )

        Spacer(modifier = Modifier.height(20.dp))
        ElasticButton(
            onClick = { goToRate() }, title = stringResource(id = R.string.rate),
            icon = R.drawable.starfill,
            iconGravity = Constants.RIGHT,
            modifier = Modifier
                .padding(top = 45.dp)
                .padding(horizontal = 37.dp)
                .fillMaxWidth()
                .requiredHeight(48.dp)
        )
    }
}