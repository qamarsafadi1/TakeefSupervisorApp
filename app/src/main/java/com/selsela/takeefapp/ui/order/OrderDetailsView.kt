package com.selsela.takeefapp.ui.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.order.model.order.Address
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.data.order.model.order.OrderService
import com.selsela.takeefapp.data.order.model.order.Payment
import com.selsela.takeefapp.data.order.model.order.Price
import com.selsela.takeefapp.data.order.model.order.WorkPeriod
import com.selsela.takeefapp.ui.common.AsyncImage
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.ui.common.StepperView
import com.selsela.takeefapp.ui.common.components.LoadingView
import com.selsela.takeefapp.ui.home.OrderUiState
import com.selsela.takeefapp.ui.home.OrderViewModel
import com.selsela.takeefapp.ui.order.cell.DateView
import com.selsela.takeefapp.ui.order.rate.RateSheet
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.Bg
import com.selsela.takeefapp.ui.theme.DividerColor
import com.selsela.takeefapp.ui.theme.DividerColorBlue
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.Red
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.SecondaryColor2
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.ui.theme.text13
import com.selsela.takeefapp.ui.theme.text13Strike
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.ui.theme.text16Bold
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.ui.theme.text16MediumStrike
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Constants.FINISHED
import com.selsela.takeefapp.utils.DateHelper
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.showError
import com.selsela.takeefapp.utils.Extensions.Companion.showSuccess
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderDetailsView(
    id: Int,
    viewModel: OrderViewModel = hiltViewModel(),
    goToCost: (Int) -> Unit,
    onRouteClick: (LatLng, LatLng) -> Unit,
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor(true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewState: OrderUiState by viewModel.uiState.collectAsStateLifecycleAware(
        OrderUiState()
    )
    val rateSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    when (viewState.state) {
        State.SUCCESS -> {
            viewState.order?.let {
                OrderDetailsContent(
                    viewModel,
                    it,
                    uiState = viewState,
                    goToCost,
                    viewModel::updateOrderStatus,
                    onBack,
                    onRouteClick
                ){

                    viewModel.getRateItem()
                    coroutineScope.launch {
                        if (rateSheetState.isVisible)
                            rateSheetState.hide()
                        else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
            }
        }

        State.LOADING -> {
            if (!viewModel.isDetailsLoaded)
                LoadingView()
            else {
                viewState.order?.let {
                    OrderDetailsContent(
                        viewModel = viewModel,
                        it,
                        uiState = viewState,
                        goToCost,
                        viewModel::updateOrderStatus,
                        onBack,
                        onRouteClick
                    ){
                        viewState.order = Order(id = it)
                        coroutineScope.launch {
                            if (rateSheetState.isVisible)
                                rateSheetState.hide()
                            else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                }
            }
        }

        else -> {}
    }


    /**
     * Handle Ui state from flow
     */

    LaunchedEffect(Unit) {
        viewModel.getOrderDetails(id)
    }

    EventEffect(
        event = viewState.onFailure,
        onConsumed = viewModel::onFailure
    ) { error ->
        Common.handleErrors(
            error.responseMessage,
            error.errors,
            context
        )
    }
//    EventEffect(
//        event = viewState.onSuccess!!,
//        onConsumed = viewModel::onSuccess
//    ) {
//    }

    RateSheet(
        rateSheetState,
        viewModel,
        viewState,
        onConfirm = viewModel::rateOrder
    )

    when (viewState.state) {
        State.SUCCESS -> {
            if (viewState.responseMessage.isNullOrEmpty().not()) {
                LocalContext.current.showSuccess(
                    viewState.responseMessage ?: ""
                )
                LaunchedEffect(key1 = Unit) {
                    coroutineScope.launch {
                        if (rateSheetState.isVisible)
                            rateSheetState.hide()
                        else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
                viewState.responseMessage = ""
                viewModel.getOrderDetails(id)
            }
        }

        else -> {}
    }
}

@Composable
private fun OrderDetailsContent(
    viewModel: OrderViewModel,
    order: Order,
    uiState: OrderUiState,
    addAdditionalCost: (Int) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit,
    onBack: () -> Unit,
    onRouteClick: (LatLng, LatLng) -> Unit,
    onRateClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(bottom = 45.dp)
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .requiredHeight(88.dp)
                    .background(Color.White)
                    .padding(top = 30.dp)
                    .padding(horizontal = 6.dp),
            ) {

                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.backbutton),
                        contentDescription = ""
                    )
                }
                Text(
                    text = stringResource(id = R.string.order_details),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = text14Meduim
                )


            }
            Card(
                modifier = Modifier
                    .padding(
                        vertical = 14.2.dp,
                        horizontal = 19.dp
                    )
                    .fillMaxSize()

            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
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
                        }
                        DateView(order)
                    }
                    if (order.case.id != 6) {
                        Spacer(modifier = Modifier.height(22.dp))
                        StepperView(
                            Modifier
                                .fillMaxWidth(),
                            items = LocalData.cases?.filter { it.id != 6 },
                            currentStep = order.logs.distinctBy { it.case.id }.lastIndex
                        )
                        if (order.case.id != FINISHED) {
                            if (order.case.id == Constants.UNDER_PROGRESS) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (order.hasMaintenance == 1 && order.price.additionalCost == 0.0) {
                                        ElasticButton(
                                            onClick = { addAdditionalCost(order.id) },
                                            title = stringResource(id = R.string.extra_cost),
                                            modifier = Modifier
                                                .paddingTop(13)
                                                .requiredHeight(36.dp)
                                                .fillMaxWidth()
                                                .weight(1f),
                                            colorBg = Purple40,)
                                        Spacer(modifier = Modifier.width(14.dp))
                                    }
                                    ElasticButton(
                                        onClick = {
                                            updateOrderStatus(
                                                order.id,
                                                if (order.payment.id == Constants.COD)
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
                                        isLoading = uiState.state == State.LOADING

                                    )
                                }
                            } else {
                                Row(
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth(),
                                ) {
                                    var title = stringResource(id = R.string.go_now)
                                    var color = Purple40
                                    when (order.case.id) {
                                        Constants.NEW_ORDER -> {
                                            title = stringResource(id = R.string.go_now)
                                            color = Purple40
                                        }

                                        Constants.ON_WAY -> {
                                            title = stringResource(id = R.string.start_order)
                                            color = LightBlue
                                        }
                                    }
                                    val context = LocalContext.current
                                    if(order.case.id == Constants.ON_WAY){

                                        ElasticButton(
                                            onClick = {
                                                onRouteClick(
                                                    LatLng(order.address.latitude, order.address.longitude),
                                                    LatLng(
                                                        order.supervisor?.latitude ?: 0.0,
                                                        order.supervisor?.longitude ?: 0.0
                                                    )
                                                )
                                            },
                                            title = stringResource(id = R.string.follow_route),
                                            icon = R.drawable.map,
                                            iconGravity = Constants.RIGHT,
                                            modifier = Modifier
                                                .paddingTop(13)
                                                .requiredHeight(36.dp)
                                                .weight(1f)
                                                .fillMaxWidth()
                                            ,
                                            buttonBg = LightBlue
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))

                                    }

                                    ElasticButton(
                                        onClick = {
                                            if (viewModel.currentOrder.value == null || viewModel.currentOrder.value?.id == order.id) {
                                                updateOrderStatus(
                                                    order.id,
                                                    if (order.payment.id == Constants.COD)
                                                        order.price.paidCash.toString()
                                                    else null
                                                )
                                            } else {
                                                context.showError(context.getString(R.string.sorry_you_have_an_order))
                                            }
                                        },
                                        title = title,
                                        modifier = Modifier
                                            .paddingTop(13)
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .requiredHeight(36.dp),
                                        colorBg = color,
                                        isLoading = uiState.state == State.LOADING
                                    )
                                }
                            }
                        }else{
                            if (order.case.canRate == 1 && order.isRated == 0) {
                                Row(
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()) {
                                    ElasticButton(
                                        onClick = { onRateClick(order.id) },
                                        title = stringResource(id = R.string.rate),
                                        icon = R.drawable.star,
                                        iconGravity = Constants.RIGHT,
                                        modifier = Modifier
                                            .paddingTop(13)
                                            .requiredHeight(36.dp)
                                            .fillMaxWidth(),
                                        buttonBg = TextColor
                                    )
                                }
                            }

                        }
                    } else {
                        Row(
                            Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
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
                    Divider(
                        thickness = 1.dp,
                        color = DividerColor,
                        modifier = Modifier.padding(top = 27.6.dp)
                    )
                    VisitDateView(order.workPeriod,order.orderDate)
                    Divider(
                        thickness = 1.dp,
                        color = DividerColor,
                        modifier = Modifier.padding(top = 8.6.dp)
                    )
                    SelectedAddressView(order.address)
                    CostView(order.price, order.payment,
                        order.additional_payment_type,
                        useWallet = order.useWallet)
                    Box(
                        modifier = Modifier
                            .padding(top = 11.2.dp)
                            .padding(horizontal = 7.dp)
                            .fillMaxWidth()
                            .requiredHeight(39.dp)
                            .background(SecondaryColor2, shape = RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.service_details),
                            style = text14,
                        )
                    }

                    Column(
                        Modifier
                            .padding(horizontal = 7.dp)
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {

                        repeat(order.orderService.size) {
                            ServiceItem(order.orderService[it])
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ServiceItem(orderService: OrderService) {
    Column(
        Modifier
            .paddingTop(11)
            .background(Color.White, shape = RoundedCornerShape(6.dp))
            .border(
                width = 1.dp, color = SecondaryColor2,
                RoundedCornerShape(6.dp)
            )
            .padding(bottom = 13.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 7.dp)
                .fillMaxWidth()
                .padding(top = 23.2.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(
                    text = stringResource(R.string.maintinance_serivce, orderService.service.name),
                    style = text12,
                    color = TextColor,
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.Text(
                    text = "${orderService.totalServicePrice}",
                    style = if (orderService.isCalculatedInTotal == 1) text16Medium else text16MediumStrike,
                    color = TextColor
                )
                Spacer(modifier = Modifier.width(3.dp))
                androidx.compose.material3.Text(
                    text = stringResource(id = R.string.currency_1, Extensions.getCurrency()),
                    style = if (orderService.isCalculatedInTotal == 1) text13 else text13Strike,
                    color = SecondaryColor
                )

            }
        }
        repeat(orderService.acType.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = orderService.acType[it].acType.name,
                        style = text12,
                        color = SecondaryColor,
                    )

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.Text(
                        text = "${orderService.acType[it].count}",
                        style = text16Medium,
                        color = TextColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    androidx.compose.material3.Text(
                        text = stringResource(R.string.device),
                        style = text13,
                        color = SecondaryColor
                    )
                }
            }
        }
    }
}


@Composable
fun CostView(price: Price,
             payment: Payment,
             additional_payment_type: Payment? = null,
             useWallet: Int) {
    Column(
        Modifier
            .padding(top = 21.dp)
            .padding(horizontal = 7.3.dp)
            .fillMaxWidth()
            .background(LightBlue.copy(.20f), shape = RoundedCornerShape(11.dp))
            .padding(bottom = 23.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 23.2.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.moneyicon),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(LightBlue.copy(0.30f))
                )
                Text(
                    text = stringResource(id = R.string.cost_1),
                    style = text14Meduim,
                    color = TextColor,
                    modifier = Modifier.padding(start = 13.3.dp)
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.Text(
                    text = "${price.grandTotal.plus(price.additionalCost)}",
                    style = text16Medium,
                    color = TextColor
                )
                Spacer(modifier = Modifier.width(3.dp))
                androidx.compose.material3.Text(
                    text = stringResource(id = R.string.currency_1, Extensions.getCurrency()),
                    style = text13,
                    color = SecondaryColor
                )

            }
        }

        Divider(
            thickness = 1.dp,
            color = DividerColorBlue,
            modifier = Modifier.paddingTop(15)
        )
        when (payment.id) {
            Constants.WALLET -> {
                WalletSection(false, price =0.0,
                    paidWallet = price.paidWallet,)
            }

            else -> {
                if (useWallet == 1) {
                    WalletSection(
                        true,
                        price = price.grandTotal - price.paidWallet,
                        payment = payment,
                        paidWallet = price.paidWallet
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.payment_method),
                                style = text12,
                                color = SecondaryColor,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            AsyncImage(
                                imageUrl = payment.iconUrl,
                                modifier = Modifier.size(33.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            androidx.compose.material3.Text(
                                text = "${price.grandTotal}",
                                style = text16Medium,
                                color = TextColor,
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            androidx.compose.material3.Text(
                                text = stringResource(
                                    id = R.string.currency_1,
                                    Extensions.getCurrency()
                                ),
                                style = text13,
                                color = SecondaryColor
                            )

                        }
                    }
                }
            }
        }
        if (price.additionalCost != 0.0){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.maintinance_cost),
                        style = text12,
                        color = SecondaryColor,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    AsyncImage(
                        imageUrl = additional_payment_type?.iconUrl ?: "",
                        modifier = Modifier.size(33.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text(
                        text = "${price.additionalCost}",
                        style = text16Medium,
                        color = TextColor,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    androidx.compose.material3.Text(
                        text = stringResource(
                            id = R.string.currency_1,
                            Extensions.getCurrency()
                        ),
                        style = text13,
                        color = SecondaryColor
                    )

                }
            }
        }

    }
}

@Composable
private fun VisitDateView(workPeriod: WorkPeriod, date: String) {
    val orderDate = if (date != "") DateHelper.getOrderDateNoraml(date) else listOf()
    Row(
        modifier = Modifier
            .padding(top = 13.dp)
            .padding(horizontal = 9.6.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.date_line),
                contentDescription = ""
            )
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.visit_date_1),
                style = text12,
                color = SecondaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )

        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Spacer(modifier = Modifier.width(25.dp))
                androidx.compose.material3.Text(
                    text = workPeriod.name,
                    style = text14,
                    color = TextColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
            Row(
                Modifier.paddingTop(
                    7
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (orderDate.isEmpty().not())
                        "${orderDate[0]}-${orderDate[1]}-${orderDate[2]}"
                    else "",
                    style = text12,
                    color = SecondaryColor
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = ".",
                    style = text12,
                    color = SecondaryColor
                )
                Spacer(modifier = Modifier.width(3.dp))
                androidx.compose.material3.Text(
                    text = "${workPeriod.getTimeFromFormatted()} - ${workPeriod.getTimeToFormatted()}",
                    style = text12,
                    color = SecondaryColor,
                )

            }

        }
    }


}

@Composable
private fun SelectedAddressView(address: Address) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.location),
            contentDescription = "",
            modifier = Modifier.padding(end = 6.6.dp, top = 20.dp)
        )
        Column(
            modifier = Modifier
                .paddingTop(20)
                .weight(1f)
        )
        {
            Text(
                text = stringResource(R.string.selected_address),
                style = text12Meduim,
                color = SecondaryColor
            )
            Text(
                text = address.note,
                style = text14,
                color = TextColor,
                modifier = Modifier.paddingTop(11)
            )
        }
    }
}


@Composable
private fun WalletSection(
    isPartition: Boolean = false, price: Double,
    paidWallet: Double? = 0.0,
            payment: Payment? = null
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.2.dp)
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.wallet_balance),
                style = text12,
                color = SecondaryColor,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            androidx.compose.material3.Text(
                text = "${paidWallet}",
                style = text16Medium,
                color = TextColor
            )
            Spacer(modifier = Modifier.width(3.dp))
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.currency_1, Extensions.getCurrency()),
                style = text13,
                color = SecondaryColor
            )

        }
    }
    if (isPartition) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.other_pay),
                    style = text12,
                    color = SecondaryColor,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                AsyncImage(
                    imageUrl = payment?.iconUrl ?: "",
                    modifier = Modifier.size(33.dp)

                )
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material3.Text(
                    text = "${price}",
                    style = text16Medium,
                    color = TextColor,
                )
                Spacer(modifier = Modifier.width(3.dp))
                androidx.compose.material3.Text(
                    text = stringResource(id = R.string.currency_1, Extensions.getCurrency()),
                    style = text13,
                    color = SecondaryColor
                )

            }
        }
    }
}
