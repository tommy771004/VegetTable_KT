package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class CreateAlertRequest(
    @SerializedName("deviceToken") val deviceToken: String,
    @SerializedName("cropName") val cropName: String,
    @SerializedName("targetPrice") val targetPrice: Double,
    @SerializedName("condition") val condition: String
)
