package com.selsela.takeefapp.ui.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.ui.common.components.EmptyView
import com.selsela.takeefapp.ui.common.components.LoadingView
import com.selsela.takeefapp.ui.home.OrderState
import com.selsela.takeefapp.ui.home.OrderUiState
import com.selsela.takeefapp.ui.home.OrderViewModel
import com.selsela.takeefapp.ui.order.cell.OrderItem
import com.selsela.takeefapp.ui.order.rate.RateSheet
import com.selsela.takeefapp.ui.splash.ChangeNavigationBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarOnlyColor
import com.selsela.takeefapp.ui.theme.Bg
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Constants.FINISHED
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.Extensions.Companion.showSuccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrdersView(
    caseID: Int,
    viewModel: OrderViewModel = hiltViewModel(),
    goToDetails: (Int) -> Unit,
    goToCost: (Int) -> Unit,
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor(true)
    val context = LocalContext.current
    val lazyColumnListState = rememberLazyListState()
    var rateSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
    )
    val viewState: OrderUiState by viewModel.uiState.collectAsStateLifecycleAware(
        OrderUiState()
    )
    val coroutineScope = rememberCoroutineScope()
    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
        }
    }
    caseID.log("caseID")
    viewModel.currentOrder.value?.log("caseID")
    val orders = when (caseID) {
        FINISHED -> viewModel.archiveOrderList
        else ->
            if (viewModel.currentOrder.value != null)
                listOf(viewModel.currentOrder.value)
            else emptyList()
    }
    orders.isEmpty().log("orders")

    OrderListContent(
        viewState,
        caseID,
        viewModel,
        lazyColumnListState = lazyColumnListState,
        orders,
        onBack,
        goToDetails,
        onRateClick = {
            viewModel.getRateItem()
            viewState.order = Order(id = it)
            coroutineScope.launch {
                if (rateSheetState.isVisible) {
                    rateSheetState.hide()
                }
                else rateSheetState.animateTo(ModalBottomSheetValue.Expanded)
            }

        },
        viewModel::updateOrderStatus,
        goToCost
    )


    LaunchedEffect(Unit) {
        if (!viewModel.isLoaded)
            viewModel.getArchiveOrders()
    }

    // check if need pagination
    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value && viewModel.listState == OrderState.IDLE)
            viewModel.getArchiveOrders()
    }
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
                viewModel.onRefreshArchive()
            }
        }

        else -> {}
    }
}

@Composable
private fun OrderListContent(
    uiState: OrderUiState,
    caseID: Int,
    viewModel: OrderViewModel,
    lazyColumnListState: LazyListState,
    orders: List<Order?>,
    onBack: () -> Unit,
    goToDetails: (Int) -> Unit,
    onRateClick: (Int) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit,
    goToCost: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        Column(Modifier.fillMaxSize()) {
            Header(caseID) {
                onBack()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 19.dp)
            ) {
                when (viewModel.listState) {
                    OrderState.LOADING -> {
                        LoadingView()
                    }

                    OrderState.IDLE, OrderState.PAGINATING -> {
                        OrderList(
                            lazyColumnListState,
                            uiState,
                            orders, goToDetails,
                            onRateClick,
                            updateOrderStatus,
                            goToCost,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrderList(
    lazyColumnListState: LazyListState,
    uiState: OrderUiState,
    orders: List<Order?>,
    goToDetails: (Int) -> Unit,
    onRateClick: (Int) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit,
    goToCost: (Int) -> Unit,
) {
    orders.size.log("orders.isEmpty()")
    if (orders.isEmpty().not()) {
        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .padding(bottom = 42.dp)
                .fillMaxHeight()

        ) {
            items(orders) {
                it?.let { order ->
                    OrderItem(
                        uiState = uiState,
                        order,
                        onClick = { goToDetails(it) },
                        onRateClick,
                        addAdditionalCost = goToCost
                    ) { id, codAmount ->
                        updateOrderStatus(id, codAmount)
                    }
                }

            }
        }
    } else {
        EmptyView(
            stringResource(R.string.no_orders),
            stringResource(R.string.no_orders_lbl),
            backgroundColor = Bg
        )
    }
}


@Composable
private fun Header(
    caseID: Int,
    onBack: () -> Unit
) {
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
            text =
            when (caseID) {
                Constants.NEW_ORDER -> stringResource(id = R.string.new_orders)
                Constants.UNDER_PROGRESS -> stringResource(R.string.ongoing_order)
                else -> stringResource(R.string.archive)
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            style = text14Meduim
        )
    }
}