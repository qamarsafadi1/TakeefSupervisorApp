package com.selsela.takeefapp.data.config.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Case(
    val canCancel: Int = 0,
    @SerializedName("can_rate")
    val canRate: Int = 0,
    val id: Int = 0,
    val name: String = ""
)