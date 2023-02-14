package com.selsela.takeefapp.ui.order.cell

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.data.order.model.order.WorkPeriod
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.SelectedServicesView
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.ui.common.StepperView
import com.selsela.takeefapp.ui.home.OrderUiState
import com.selsela.takeefapp.ui.order.rate.RateSheet
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.Red
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text10
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text12Bold
import com.selsela.takeefapp.ui.theme.text16Bold
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Constants.COD
import com.selsela.takeefapp.utils.Constants.FINISHED
import com.selsela.takeefapp.utils.Constants.NEW_ORDER
import com.selsela.takeefapp.utils.Constants.ON_WAY
import com.selsela.takeefapp.utils.Constants.UNDER_PROGRESS
import com.selsela.takeefapp.utils.Extensions.Companion.getCurrency
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.Extensions.Companion.showError
import com.selsela.takeefapp.utils.Extensions.Companion.showSuccess
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.launch

@Composable
fun OrderItem(
    uiState: OrderUiState,
    currentOrder: Order,
    onClick: () -> Unit,
    onRouteClick: (LatLng, LatLng) -> Unit,
    addAdditionalCost: (Int) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 11.dp, top = 11.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 191.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 191.dp)
                .padding(
                    horizontal = 10.dp,
                    vertical = 21.dp
                )
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier
                        .wrapContentWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.order_number),
                        style = text11,
                        color = SecondaryColor
                    )
                    Text(
                        text = "#${currentOrder.number}",
                        style = text16Bold,
                        color = TextColor
                    )
                    DateView(currentOrder)

                }


                if (currentOrder.case.id != 6) {
                    StepperView(
                        Modifier
                            .fillMaxWidth()
                            .weight(1.5f),
                        items = LocalData.cases?.filter { it.id != 6 },
                        currentStep = currentOrder.logs.distinctBy { it.case.id }.lastIndex
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 32.dp)
                            .background(Red.copy(.10f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.canceled),
                            style = text12,
                            color = Red
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .paddingTop(10)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 63.dp)
                    .background(
                        LightBlue.copy(0.10f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.3.dp, vertical = 11.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.date_line),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(SecondaryColor)
                        )
                        Text(
                            text = stringResource(id = R.string.visit_date_1),
                            style = text11,
                            color = SecondaryColor,
                            modifier = Modifier.padding(start = 6.4.dp)
                        )

                    }
                    DateTimeView(currentOrder.orderDate, currentOrder.workPeriod)
                }

                Row(
                    Modifier
                        .paddingTop(10)
                        .fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moneyicon),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.cost_dot),
                        style = text11,
                        color = TextColor,
                        modifier = Modifier.padding(start = 9.dp)
                    )

                    Row {
                        Text(
                            text = "${currentOrder.grandTotal.plus(currentOrder.price.additionalCost)}",
                            style = text12Bold,
                            color = TextColor,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.currency_1, getCurrency()),
                            style = text10,
                            color = TextColor,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            }

            SelectedServicesView(orderServices = currentOrder.orderService)

            if (currentOrder.case.id != 6) {
                if (currentOrder.case.id != FINISHED) {
                    if (currentOrder.case.id == UNDER_PROGRESS) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (currentOrder.hasMaintenance == 1 && currentOrder.price.additionalCost == 0.0) {
                                ElasticButton(
                                    onClick = { addAdditionalCost(currentOrder.id) },
                                    title = stringResource(id = R.string.extra_cost),
                                    modifier = Modifier
                                        .paddingTop(13)
                                        .requiredHeight(36.dp)
                                        .fillMaxWidth()
                                        .weight(1f),
                                    colorBg = Purple40,
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                            }
                            ElasticButton(
                                onClick = {
                                    updateOrderStatus(
                                        currentOrder.id,
                                        if (currentOrder.payment.id == COD)
                                            currentOrder.price.paidCash.toString()
                                        else null
                                    )
                                },
                                title = stringResource(R.string.finish_order),
                                modifier = Modifier
                                    .paddingTop(13)
                                    .requiredHeight(36.dp)
                                    .fillMaxWidth()
                                    .weight(1f),
                                colorBg = TextColor,
                                isLoading = uiState.state == State.LOADING

                            )
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            var title = stringResource(id = R.string.go_now)
                            var color = Purple40
                            when (currentOrder.case.id) {
                                NEW_ORDER -> {
                                    title = stringResource(id = R.string.go_now)
                                    color = Purple40
                                }

                                ON_WAY -> {
                                    title = stringResource(id = R.string.start_order)
                                    color = LightBlue
                                }
                            }
                            Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End) {
                                ElasticButton(
                                    onClick = {
                                        updateOrderStatus(
                                            currentOrder.id,
                                            if (currentOrder.payment.id == COD)
                                                currentOrder.price.paidCash.toString()
                                            else null
                                        )
                                    },
                                    title = title,
                                    modifier = Modifier
                                        .paddingTop(13)
                                        .width(137.dp)
                                        .requiredHeight(36.dp),
                                    colorBg = color,
                                    isLoading = uiState.state == State.LOADING && uiState.orderId == currentOrder.id
                                )
                                if(currentOrder.case.id == ON_WAY){
                                    Spacer(modifier = Modifier.width(14.dp))

                                    ElasticButton(
                                        onClick = {
                                            onRouteClick(
                                                LatLng(currentOrder.address.latitude, currentOrder.address.longitude),
                                                LatLng(
                                                    currentOrder.supervisor?.latitude ?: 0.0,
                                                    currentOrder.supervisor?.longitude ?: 0.0
                                                )
                                            )
                                        },
                                        title = stringResource(id = R.string.follow_route),
                                        icon = R.drawable.map,
                                        iconGravity = Constants.RIGHT,
                                        modifier = Modifier
                                            .paddingTop(13)
                                            .requiredHeight(36.dp)
                                            .defaultMinSize( minWidth =  if (LocalData.appLocal == "ar") 137.dp else 157.dp),
                                        buttonBg = LightBlue
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderItem(
    uiState: OrderUiState,
    currentOrder: Order,
    onClick: (Int) -> Unit,
    onRateClick: (Int) -> Unit,
    addAdditionalCost: (Int) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit
) {
    val rateSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    Card(
        modifier = Modifier
            .padding(bottom = 11.dp, top = 11.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 191.dp)
            .clickable {
                onClick(currentOrder.id)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 191.dp)
                .padding(
                    horizontal = 10.dp,
                    vertical = 21.dp
                )
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier
                        .wrapContentWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.order_number),
                        style = text11,
                        color = SecondaryColor
                    )
                    Text(
                        text = "#${currentOrder.number}",
                        style = text16Bold,
                        color = TextColor
                    )
                    DateView(currentOrder)

                }


                if (currentOrder.case.id != 6) {
                    StepperView(
                        Modifier
                            .fillMaxWidth()
                            .weight(1.5f),
                        items = LocalData.cases?.filter { it.id != 6 },
                        currentStep = currentOrder.logs.distinctBy { it.case.id }.lastIndex
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 32.dp)
                            .background(Red.copy(.10f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.canceled),
                            style = text12,
                            color = Red
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .paddingTop(10)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 63.dp)
                    .background(
                        LightBlue.copy(0.10f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.3.dp, vertical = 11.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.date_line),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(SecondaryColor)
                        )
                        Text(
                            text = stringResource(id = R.string.visit_date_1),
                            style = text11,
                            color = SecondaryColor,
                            modifier = Modifier.padding(start = 6.4.dp)
                        )

                    }
                    DateTimeView(currentOrder.orderDate, currentOrder.workPeriod)
                }

                Row(
                    Modifier
                        .paddingTop(10)
                        .fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moneyicon),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.cost_dot),
                        style = text11,
                        color = TextColor,
                        modifier = Modifier.padding(start = 9.dp)
                    )

                    Row {
                        Text(
                            text = "${currentOrder.grandTotal.plus(currentOrder.price.additionalCost)}",
                            style = text12Bold,
                            color = TextColor,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.currency_1, getCurrency()),
                            style = text10,
                            color = TextColor,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            }

            SelectedServicesView(orderServices = currentOrder.orderService)

            if (currentOrder.case.id != 6) {
                if (currentOrder.case.id != FINISHED) {
                    if (currentOrder.case.id == UNDER_PROGRESS) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (currentOrder.hasMaintenance == 1 && currentOrder.price.additionalCost == 0.0) {
                                ElasticButton(
                                    onClick = { addAdditionalCost(currentOrder.id) },
                                    title = stringResource(id = R.string.extra_cost),
                                    modifier = Modifier
                                        .paddingTop(13)
                                        .requiredHeight(36.dp)
                                        .fillMaxWidth()
                                        .weight(1f),
                                    colorBg = Purple40
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                            }
                            ElasticButton(
                                onClick = {
                                    updateOrderStatus(
                                        currentOrder.id,
                                        if (currentOrder.payment.id == COD)
                                            currentOrder.price.paidCash.toString()
                                        else null
                                    )
                                },
                                title = stringResource(R.string.finish_order),
                                modifier = Modifier
                                    .paddingTop(13)
                                    .requiredHeight(36.dp)
                                    .fillMaxWidth()
                                    .weight(1f),
                                colorBg = TextColor,
                                isLoading = uiState.state == State.LOADING && uiState.caseId+1 == FINISHED

                            )
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            var title = stringResource(id = R.string.go_now)
                            var color = Purple40
                            when (currentOrder.case.id) {
                                NEW_ORDER -> {
                                    title = stringResource(id = R.string.go_now)
                                    color = Purple40
                                }

                                ON_WAY -> {
                                    title = stringResource(id = R.string.start_order)
                                    color = LightBlue
                                }
                            }
                            ElasticButton(
                                onClick = {
                                    updateOrderStatus(
                                        currentOrder.id,
                                        if (currentOrder.payment.id == COD)
                                            currentOrder.price.paidCash.toString()
                                        else null
                                    )
                                },
                                title = title,
                                modifier = Modifier
                                    .paddingTop(13)
                                    .width(137.dp)
                                    .requiredHeight(36.dp),
                                colorBg = color,
                                isLoading = uiState.state == State.LOADING && uiState.orderId == currentOrder.id
                            )
                        }
                    }
                } else {
                    if (currentOrder.case.canRate == 1 && currentOrder.isRated == 0) {
                        Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End) {
                            ElasticButton(
                                onClick = { onRateClick(currentOrder.id) },
                                title = stringResource(id = R.string.rate),
                                icon = R.drawable.star,
                                iconGravity = Constants.RIGHT,
                                modifier = Modifier
                                    .paddingTop(13)
                                    .requiredHeight(36.dp)
                                    .width(137.dp),
                                buttonBg = TextColor
                            )
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun NextOrderItem(
    viewState: OrderUiState,
    currentOrder: Order?,
    order: Order,
    onClick: () -> Unit,
    updateOrderStatus: (Int, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 11.dp, top = 11.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 191.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 191.dp)
                .padding(
                    horizontal = 10.dp,
                    vertical = 21.dp
                )
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier
                        .wrapContentWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.order_number),
                        style = text11,
                        color = SecondaryColor
                    )
                    Text(
                        text = "#${order.number}",
                        style = text16Bold,
                        color = TextColor
                    )
                    DateView(order)

                }

                StepperView(
                    Modifier
                        .fillMaxWidth(),
                    items = LocalData.cases?.filter { it.id != 6 },
                    currentStep = order.logs.distinctBy { it.case.id }.lastIndex
                )
            }

            Column(
                modifier = Modifier
                    .paddingTop(10)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 63.dp)
                    .background(
                        LightBlue.copy(0.10f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.3.dp, vertical = 11.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.date_line),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(SecondaryColor)
                        )
                        Text(
                            text = stringResource(id = R.string.visit_date_1),
                            style = text11,
                            color = SecondaryColor,
                            modifier = Modifier.padding(start = 6.4.dp)
                        )

                    }
                    DateTimeView(orderDate = order.orderDate, order.workPeriod)
                }

                Row(
                    Modifier
                        .paddingTop(10)
                        .fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moneyicon),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.cost_dot),
                        style = text11,
                        color = TextColor,
                        modifier = Modifier.padding(start = 9.dp)
                    )

                    Row {
                        Text(
                            text = "${order.grandTotal.plus(order.price.additionalCost)}",
                            style = text12Bold,
                            color = TextColor,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.currency_1, getCurrency()),
                            style = text10,
                            color = TextColor,
                            modifier = Modifier.padding(start = 5.dp)
                        )

                    }

                }
            }


            SelectedServicesView(orderServices = order.orderService)

            val context = LocalContext.current
            if (order.case.id == UNDER_PROGRESS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (order.hasMaintenance == 1) {
                        ElasticButton(
                            onClick = {
                                updateOrderStatus(
                                    order.id,
                                    if (order.payment.id == COD)
                                        order.price.paidCash.toString()
                                    else null
                                )
                            },
                            title = stringResource(id = R.string.extra_cost),
                            modifier = Modifier
                                .paddingTop(13)
                                .requiredHeight(36.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            colorBg = Purple40
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                    }
                    ElasticButton(
                        onClick = {
                            if (currentOrder != null)
                                context.showError(context.getString(R.string.sorry_you_have_an_order))
                            updateOrderStatus(
                                order.id,
                                if (order.payment.id == COD)
                                    order.price.paidCash.toString()
                                else null
                            )
                        },
                        title = stringResource(R.string.finish_order),
                        modifier = Modifier
                            .paddingTop(13)
                            .requiredHeight(36.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        colorBg = TextColor,
                        isLoading = viewState.state == State.LOADING && viewState.caseId+1 == FINISHED
                    )
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    var title = stringResource(id = R.string.go_now)
                    var color = Purple40
                    when (order.case.id) {
                        NEW_ORDER -> {
                            title = stringResource(id = R.string.go_now)
                            color = Purple40
                        }

                        ON_WAY -> {
                            title = stringResource(id = R.string.start_order)
                            color = LightBlue
                        }
                    }
                    ElasticButton(
                        onClick = {
                            if (currentOrder != null)
                                context.showError(context.getString(R.string.sorry_you_have_an_order))
                            else updateOrderStatus(
                                order.id,
                                if (order.payment.id == COD)
                                    order.price.paidCash.toString()
                                else null
                            )

                        },
                        title = title,
                        modifier = Modifier
                            .paddingTop(13)
                            .width(137.dp)
                            .requiredHeight(36.dp),
                        colorBg = color,
                        isLoading = viewState.state == State.LOADING && viewState.orderId == order.id
                    )
                }
            }
        }
    }
}

@Composable
fun DateView(currentOrder: Order) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = currentOrder.getOrderPmAm(),
            style = text11,
            color = SecondaryColor
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = currentOrder.getOrderTimeOnly(),
            style = text11,
            color = SecondaryColor
        )
        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = ".",
            style = text11,
            color = SecondaryColor
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = currentOrder.getOrderDateOnly(),
            style = text11,
            color = SecondaryColor
        )
    }
}

@Composable
private fun DateTimeView(orderDate: String, fromTo: WorkPeriod) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "${fromTo.getTimeFromFormatted()} - ${fromTo.getTimeToFormatted()}",
            style = text12,
            color = TextColor
        )
        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = ".",
            style = text12,
            color = TextColor
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = orderDate,
            style = text12,
            color = TextColor
        )
    }
}

