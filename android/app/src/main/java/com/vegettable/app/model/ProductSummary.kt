package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class ProductSummary(
    @SerializedName("cropCode") val cropCode: String,
    @SerializedName("cropName") val cropName: String,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("prevAvgPrice") val prevAvgPrice: Double,
    @SerializedName("historicalAvgPrice") val historicalAvgPrice: Double,
    @SerializedName("volume") val volume: Double,
    @SerializedName("priceLevel") val priceLevel: String?,
    @SerializedName("trend") val trend: String?,
    @SerializedName("recentPrices") val recentPrices: List<DailyPrice>? = null,
    @SerializedName("category") val category: String?,
    @SerializedName("subCategory") val subCategory: String?,
    @SerializedName("aliases") val aliases: List<String>? = null
)
