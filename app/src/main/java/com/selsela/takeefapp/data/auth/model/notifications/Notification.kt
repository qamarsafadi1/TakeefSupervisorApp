package com.selsela.takeefapp.data.auth.model.notifications


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Notification(
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("is_read")
    val isRead: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("title")
    val title: String = ""
)