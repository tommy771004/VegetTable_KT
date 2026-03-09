package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class DailyPrice(
    @SerializedName("date") val date: String?,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("volume") val volume: Double
)
