package com.selsela.takeefapp.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selsela.takeefapp.data.order.model.order.Order
import com.selsela.takeefapp.data.order.repository.OrderRepository
import com.selsela.takeefapp.utils.retrofit.model.ErrorsData
import com.selsela.takeefapp.utils.retrofit.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.selsela.jobsapp.utils.validateRequired
import com.selsela.takeefapp.ui.common.State
import com.selsela.takeefapp.utils.Constants.FINISHED
import com.selsela.takeefapp.utils.Constants.ON_WAY
import com.selsela.takeefapp.utils.Constants.UNDER_PROGRESS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.Red
import de.palm.composestateevents.StateEvent


/**
 * UiState for the Order
 */

enum class OrderState {
    IDLE,
    LOADING,
    PAGINATING,
}

data class OrderUiState(
    val state: State = State.IDLE,
    var responseMessage: String? = "",
    var order: Order? = null,
    val onSuccess: StateEvent? = consumed,
    val onFailure: StateEventWithContent<ErrorsData> = consumed(),
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val application: Application,
    private val repository: OrderRepository
) : ViewModel() {


    /**
     * Order Pagination Variables
     */

    val orderList = mutableStateListOf<Order>()
    var currentOrder = mutableStateOf<Order?>(null)
    var isLoaded = false
    var isDetailsLoaded = false
    private var page by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var additionalCost by mutableStateOf("")
    var listState by mutableStateOf(OrderState.IDLE)
    var errorMessage: MutableState<String> = mutableStateOf("")
    var isValid: MutableState<Boolean> = mutableStateOf(true)


    /**
     * State Subscribers
     */
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()


    private var state: OrderUiState
        get() = _uiState.value
        set(newState) {
            _uiState.update { newState }
        }


    /**
     * Validation
     */

    private fun isCostValid(): Boolean {
        val message = additionalCost.validateRequired(
            application.applicationContext,
            application.getString(R.string.additional_cost)
        )
        if (message == "") {
            isValid.value = true
        } else {
            isValid.value = false
            errorMessage.value = message
        }
        return isValid.value
    }

    fun validateBorderColor(): Color {
        return if (errorMessage.value.isNotEmpty() && isValid.value.not())
            Red
        else BorderColor
    }


    /**
     * API Requests
     */

    fun getOrders() = viewModelScope.launch {
        if (page == 1 || (page != 1 && canPaginate) && listState == OrderState.IDLE) {
            listState = if (page == 1) OrderState.LOADING else OrderState.PAGINATING
            repository.getOrders(page).collect { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        canPaginate = result.data?.hasMorePage ?: false
                        currentOrder.value = result.data?.processingOrder
                        if (page == 1) {
                            orderList.clear()
                            result.data?.orders?.let { orderList.addAll(it) }
                        } else {
                            result.data?.orders?.let { orderList.addAll(it) }
                        }
                        listState = OrderState.IDLE
                        if (canPaginate)
                            page++

                    }

                    Status.LOADING ->
                        listState = OrderState.LOADING


                    Status.ERROR -> {
                        OrderUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                ),
                            ),
                        )
                    }
                }
            }
        }
    }

    fun updateOrderStatus(id: Int, amount: String? = null) {
        viewModelScope.launch {
            state = state.copy(
                state = State.LOADING
            )
            repository.updateOrderStatus(id)
                .collect { result ->
                    val orderStateUi = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            when (result.data?.order?.case?.id) {
                                FINISHED -> {
                                    currentOrder.value = null
                                }

                                ON_WAY, UNDER_PROGRESS -> {
                                    if (currentOrder.value == null) {
                                        orderList.removeIf {
                                            it.id == result.data.order.id
                                        }
                                    }
                                    currentOrder.value = result.data.order
                                    currentOrder.value?.grandTotal =
                                        result.data.order.price.grandTotal
                                    currentOrder.value?.logs = result.data.order.logs

                                }
                            }

                            OrderUiState(
                                order = result.data?.order,
                                state = State.SUCCESS
                            )
                        }

                        Status.LOADING ->
                            OrderUiState(
                                state = State.LOADING
                            )

                        Status.ERROR -> OrderUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            ),
                        )
                    }
                    state = orderStateUi
                }
        }
    }

    fun addAdditionalCost(id: Int) {
        if (isCostValid()) {
            viewModelScope.launch {
                state = state.copy(
                    state = State.LOADING
                )
                repository.addAdditionalCost(id, additionalCost)
                    .collect { result ->
                        val orderStateUi = when (result.status) {
                            Status.SUCCESS -> {
                                isLoaded = true
                                additionalCost = ""
                                OrderUiState(
                                    order = result.data?.order,
                                    state = State.SUCCESS,
                                    onSuccess = triggered,
                                )

                            }

                            Status.LOADING ->
                                OrderUiState(
                                    state = State.LOADING
                                )

                            Status.ERROR -> OrderUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                            )
                        }
                        state = orderStateUi
                    }
            }
        }

    }

    fun cancelOrder(id: Int) {
        viewModelScope.launch {
            state = state.copy(
                state = State.LOADING
            )
            repository.cancelOrder(id)
                .collect { result ->
                    val orderStateUi = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            OrderUiState(
                                order = result.data?.order,
                                state = State.SUCCESS
                            )
                        }

                        Status.LOADING ->
                            OrderUiState(
                                state = State.LOADING
                            )

                        Status.ERROR -> OrderUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            ),
                        )
                    }
                    state = orderStateUi
                }
        }
    }

    fun getOrderDetails(id: Int) {
        isDetailsLoaded = false
        viewModelScope.launch {
            state =  OrderUiState(
                state = State.LOADING
            )
            repository.getOrderDetails(id)
                .collect { result ->
                    val orderStateUi = when (result.status) {
                        Status.SUCCESS -> {
                            isDetailsLoaded = true
                            isLoaded = true
                            OrderUiState(
                                order = result.data?.order,
                                state = State.SUCCESS,
                                onSuccess = triggered
                            )
                        }

                        Status.LOADING ->
                            OrderUiState(
                                state = State.LOADING
                            )

                        Status.ERROR -> OrderUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            ),
                        )
                    }
                    state = orderStateUi
                }
        }
    }

    override fun onCleared() {
        page = 1
        listState = OrderState.IDLE
        canPaginate = false
        super.onCleared()
    }


    fun onRefresh() {
        page = 1
        listState = OrderState.IDLE
        canPaginate = false
        getOrders()
    }

    fun onSuccess() {
        state = state.copy(onSuccess = consumed)
       // isLoaded = false
    }

    fun onFailure() {
        state = state.copy(onFailure = consumed())
    }
}