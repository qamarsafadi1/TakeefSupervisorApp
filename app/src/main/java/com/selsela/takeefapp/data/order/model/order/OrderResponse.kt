package com.selsela.takeefapp.data.order.model.order


import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("has_more_new_orders")
    val hasMorePage: Boolean = false,
    @SerializedName("has_more_archive_orders")
    val hasMoreArchivePage: Boolean = false,
    @SerializedName("new_orders")
    val orders: List<Order> = listOf(),
    @SerializedName("archive_orders")
    val archiveOrders: List<Order> = listOf(),
    @SerializedName("order")
    val order: Order = Order(),
    @SerializedName("processing_order")
    val processingOrder: Order? = null,
    @SerializedName("response_message")
    val responseMessage: String = "",
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("trans")
    val transaction: Transaction? = null
)