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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.LatLng
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
import com.selsela.takeefapp.ui.theme.NoRippleTheme
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.LocalData
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    vm: AuthViewModel = hiltViewModel(),
    orderVm: OrderViewModel = hiltViewModel(),
    goToMyAccount: () -> Unit,
    goToDetails: (Int) -> Unit,
    onPending: () -> Unit,
    goToCost: (Int) -> Unit,
    goToOrderRoute: (LatLng, LatLng) -> Unit,

    ) {
    Color.Transparent.ChangeStatusBarColor(true)
    val context = LocalContext.current
    val lazyColumnListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            orderVm.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
        }
    }
    val orders = orderVm.orderList
    val viewState: OrderUiState by orderVm.uiState.collectAsStateLifecycleAware(
        OrderUiState()
    )

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
    val refreshing by remember { mutableStateOf(false) } // a flag  to start or stop refreshing

    HomeContent(
        refreshing,
        onRefresh = {
            orderVm.onRefresh()
        },
        viewState,
        orderVm,
        orders,
        vm::changeAvailableStatus,
        orderVm::updateOrderStatus,
        goToMyAccount,
        goToCost,
        goToOrderRoute,
        goToDetails,
    )

    HandleNotification(onPending)
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = "new_order",
    ) {
        orderVm.onRefresh()
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


    Extensions.OnLifecycleEvent { _, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                orderVm.onRefresh()
            }

            else -> {}
        }
    }


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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeContent(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    viewState: OrderUiState,
    viewModel: OrderViewModel,
    orders: SnapshotStateList<Order>,
    onStatusChanged: (String) -> Unit,
    updateOrderStatus: (Int, String?) -> Unit,
    goToMyAccount: () -> Unit,
    goToCost: (Int) -> Unit,
    goToOrderRoute: (LatLng, LatLng) -> Unit,
    goToDetails: (Int) -> Unit
) {
    val currentOrder = viewModel.currentOrder.value
    Color.White.ChangeStatusBarColor(true)
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        onRefresh()
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    // hide ripple effect
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
//        Box(Modifier.pullRefresh(state)) {
////            Box(
////                modifier = Modifier
////                    .background(Color.White)
////            ) {
////                Column(
////                    modifier = Modifier
////                        .padding(horizontal = 19.dp)
////                        .padding(top = 25.dp)
////                        .fillMaxSize(),
////                    horizontalAlignment = Alignment.CenterHorizontally
////                ) {
////
////                    // Header
////                    Box(
////                        modifier = Modifier.fillMaxWidth(),
////                    ) {
////                        ElasticView(
////                            modifier = Modifier.align(Alignment.TopStart),
////                            onClick = { goToMyAccount() }) {
////                            Image(
////                                painter = painterResource(id = R.drawable.menu),
////                                contentDescription = "",
////                            )
////                        }
////                        Image(
////                            painter = painterResource(id = R.drawable.logosmall),
////                            contentDescription = "",
////                            modifier = Modifier.align(Alignment.TopCenter)
////                        )
////                        Switch(
////                            onStatusChanged,
////                            Modifier.align(Alignment.TopEnd)
////                        )
////                    }
////                    Spacer(modifier = Modifier.height(30.dp))
////
////                    when (viewModel.listState) {
////                        OrderState.IDLE, OrderState.PAGINATING -> {
////                            if (orders.isEmpty() && currentOrder == null)
////                                EmptyView(
////                                    stringResource(R.string.no_orders),
////                                    stringResource(R.string.no_orders_lbl),
////                                    backgroundColor = Color.White
////                                )
////                            else OrdersList(
////                                viewState,
////                                currentOrder,
////                                updateOrderStatus,
////                                goToCost,
////                                goToOrderRoute,
////                                orders,
////                                goToDetails
////                            )
////
////                        }
////
////                        OrderState.LOADING -> {
////                            LoadingView()
////                        }
////                    }
////// the refresh indicator
////
////                    // Order list
////                }
////
////
////            }
//            PullRefreshIndicator(
//                refres,
//                state,
//                Modifier
//                    .padding(top = 90.dp)
//                    .align(Alignment.TopCenter),
//            )
//        }


        Box(Modifier.pullRefresh(state)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 19.dp)
                    .padding(top = 25.dp)
            ) {
                //     Header
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

                when (viewModel.listState) {
                    OrderState.IDLE, OrderState.PAGINATING -> {
                        OrdersList(
                            viewState,
                            currentOrder,
                            updateOrderStatus,
                            goToCost,
                            goToOrderRoute,
                            orders,
                            goToDetails
                        )
                    }
                    OrderState.LOADING -> {
                        LoadingView()
                    }
                }

            }
            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrdersList(
    viewState: OrderUiState,
    currentOrder: Order?,
    updateOrderStatus: (Int, String?) -> Unit,
    goToCost: (Int) -> Unit,
    goToOrderRoute: (LatLng, LatLng) -> Unit,
    orders: SnapshotStateList<Order>,
    goToDetails: (Int) -> Unit
) {
        LazyColumn(
            Modifier
                .fillMaxSize()
        ) {
            if (currentOrder != null) {
                item {
                    Text(
                        text = stringResource(R.string.current_order),
                        style = text12Meduim,
                        color = SecondaryColor
                    )
                    OrderItem(
                        viewState,
                        currentOrder,
                        onClick = {
                            goToDetails(currentOrder.id)
                        },
                        onRouteClick = { myLatLng, supervisorLatLbg ->
                            goToOrderRoute(myLatLng, supervisorLatLbg)
                        },
                        addAdditionalCost = goToCost
                    ) { id, codAmount ->
                        updateOrderStatus(id, codAmount)
                    }
                }
            }
            if (orders.isEmpty().not()) {
                item {
                    Text(
                        text = stringResource(R.string.upcoming_orders),
                        style = text12Meduim,
                        color = SecondaryColor
                    )
                }
                items(orders) {
                    NextOrderItem(
                        viewState,
                        currentOrder,
                        it,
                        onClick = {
                            goToDetails(it.id)
                        }) { id, codAmount ->
                        updateOrderStatus(id, codAmount)
                    }
                }
            }
            else item {
                EmptyView(
                    stringResource(R.string.no_orders),
                    stringResource(R.string.no_orders_lbl),
                    backgroundColor = Color.White
                )
            }
        }

}


