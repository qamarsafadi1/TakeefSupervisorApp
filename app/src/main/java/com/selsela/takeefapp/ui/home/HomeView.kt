package com.selsela.takeefapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qamar.elasticview.ElasticView
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.ui.auth.AuthViewModel
import com.selsela.takeefapp.ui.auth.BlockedDialog
import com.selsela.takeefapp.ui.common.components.EmptyView
import com.selsela.takeefapp.ui.common.components.LoadingView
import com.selsela.takeefapp.ui.common.components.Switch
import com.selsela.takeefapp.ui.order.cell.NextOrderItem
import com.selsela.takeefapp.ui.order.cell.OrderItem
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.Bg
import com.selsela.takeefapp.ui.theme.NoRippleTheme
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.LocalData

@Composable
fun HomeView(
    vm: AuthViewModel = hiltViewModel(),
    orderVm: OrderViewModel = hiltViewModel(),
    goToRoute: () -> Unit,
    goToMyAccount: () -> Unit,
    goToDetails: () -> Unit,
    onPending: () -> Unit,
    goToCost: () -> Unit,
) {
    val lazyColumnListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            orderVm.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
        }
    }
    val orders = orderVm.orderList

    // get orders from api
    LaunchedEffect(Unit) {
        if (!orderVm.isLoaded)
            orderVm.getOrders()
    }
    // check if need pagination
    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value && orderVm.listState == OrderState.IDLE)
            orderVm.getOrders()
    }

    HomeContent(
        orderVm,
        orders,
        vm::changeAvailableStatus,
        goToMyAccount,
        goToRoute,
        goToCost,
        goToDetails,
    )
    HandleNotification(onPending)


}

@Composable
private fun HandleNotification(onPending: () -> Unit) {
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.UN_VERIFIED_MANAGEMENT,
    ) {
        onPending()
    }
    var isBlocked by remember {
        mutableStateOf(LocalData.user?.isBlock == 1)
    }
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.BLOCKED,
    ) {
        isBlocked = true
    }
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.UN_BLOCKED,
    ) {
        isBlocked = false
    }
    BlockedDialog(isBlocked)
}

@Composable
private fun HomeContent(
    viewModel: OrderViewModel,
    orders: SnapshotStateList<Order>,
    onStatusChanged: (String) -> Unit,
    goToMyAccount: () -> Unit,
    goToRoute: () -> Unit,
    goToCost: () -> Unit,
    goToDetails: () -> Unit
) {
    val currentOrder = viewModel.currentOrder.value
    Color.White.ChangeStatusBarColor()
    // hide ripple effect
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 19.dp)
                    .padding(top = 25.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ElasticView(
                        modifier = Modifier.align(Alignment.TopStart),
                        onClick = { goToMyAccount() }) {
                        Image(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "",
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logosmall),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    Switch(
                        onStatusChanged,
                        Modifier.align(Alignment.TopEnd)
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

                // Order list
                when (viewModel.listState) {
                    OrderState.IDLE, OrderState.PAGINATING -> {
                        OrdersList(currentOrder, goToRoute, goToCost, orders, goToDetails)
                    }

                    OrderState.LOADING -> {
                        LoadingView()
                    }
                }
            }

        }
    }
}

@Composable
private fun OrdersList(
    currentOrder: Order?,
    goToRoute: () -> Unit,
    goToCost: () -> Unit,
    orders: SnapshotStateList<Order>,
    goToDetails: () -> Unit
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        if (currentOrder != null) {
            item {
                Text(
                    text = stringResource(R.string.current_order),
                    style = text12Meduim,
                    color = SecondaryColor
                )
                OrderItem(
                    currentOrder,
                    onClick = {
                        goToRoute()
                    }) {
                    goToCost()
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.upcoming_orders),
                style = text12Meduim,
                color = SecondaryColor
            )
        }
        if (orders.isNotEmpty()) {
            items(orders) {
                NextOrderItem(
                    currentOrder,
                    it,
                    onClick = {
                        goToDetails()
                    }) {

                }
            }
        } else {
            item {
                EmptyView(
                    stringResource(R.string.no_orders),
                    stringResource(R.string.no_orders_lbl),
                    backgroundColor = Color.White
                )
            }
        }

    }
}


