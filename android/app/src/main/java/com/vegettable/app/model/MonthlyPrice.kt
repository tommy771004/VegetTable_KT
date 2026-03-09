package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class MonthlyPrice(
    @SerializedName("month") val month: String?,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("volume") val volume: Double
)
