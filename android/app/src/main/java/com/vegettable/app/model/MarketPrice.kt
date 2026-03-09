package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class MarketPrice(
    @SerializedName("marketName") val marketName: String?,
    @SerializedName("cropName") val cropName: String?,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("upperPrice") val upperPrice: Double,
    @SerializedName("lowerPrice") val lowerPrice: Double,
    @SerializedName("volume") val volume: Double,
    @SerializedName("transDate") val transDate: String?
)
