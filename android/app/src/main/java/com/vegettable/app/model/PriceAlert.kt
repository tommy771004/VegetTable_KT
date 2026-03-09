package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class PriceAlert(
    @SerializedName("id") val id: Int,
    @SerializedName("cropName") val cropName: String?,
    @SerializedName("targetPrice") val targetPrice: Double,
    @SerializedName("condition") val condition: String?, // "below" | "above"
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("lastTriggeredAt") val lastTriggeredAt: String?,
    @SerializedName("createdAt") val createdAt: String?
)
