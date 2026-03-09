package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class PricePrediction(
    @SerializedName("cropName") val cropName: String?,
    @SerializedName("currentPrice") val currentPrice: Double,
    @SerializedName("predictedPrice") val predictedPrice: Double,
    @SerializedName("changePercent") val changePercent: Double,
    @SerializedName("direction") val direction: String?, // "up" | "down" | "stable"
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("reasoning") val reasoning: String?
)
