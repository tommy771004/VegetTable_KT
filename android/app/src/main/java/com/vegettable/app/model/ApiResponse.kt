package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

/**
 * .NET API 統一回應格式
 */
data class ApiResponse<T>(
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long
)
