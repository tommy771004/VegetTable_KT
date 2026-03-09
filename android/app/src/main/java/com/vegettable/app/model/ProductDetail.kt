package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class ProductDetail(
    @SerializedName("cropCode") val cropCode: String?,
    @SerializedName("cropName") val cropName: String?,
    @SerializedName("aliases") val aliases: List<String>? = null,
    @SerializedName("category") val category: String?,
    @SerializedName("subCategory") val subCategory: String?,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("historicalAvgPrice") val historicalAvgPrice: Double,
    @SerializedName("priceLevel") val priceLevel: String?,
    @SerializedName("trend") val trend: String?,
    @SerializedName("dailyPrices") val dailyPrices: List<DailyPrice>? = null,
    @SerializedName("monthlyPrices") val monthlyPrices: List<MonthlyPrice>? = null
)
