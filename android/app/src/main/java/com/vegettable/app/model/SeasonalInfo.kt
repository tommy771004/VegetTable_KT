package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class SeasonalInfo(
    @SerializedName("cropName") val cropName: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("peakMonths") val peakMonths: List<Int>? = null,
    @SerializedName("isInSeason") val isInSeason: Boolean,
    @SerializedName("seasonNote") val seasonNote: String?
)
