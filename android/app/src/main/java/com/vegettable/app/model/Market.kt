package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class Market(
    @SerializedName("marketCode") val marketCode: String?,
    @SerializedName("marketName") val marketName: String?,
    @SerializedName("region") val region: String?
)
