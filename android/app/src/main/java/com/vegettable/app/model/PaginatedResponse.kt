package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

/**
 * 分頁回應包裝 — 包含資料列表 + 分頁元數據
 */
data class PaginatedResponse<T>(
    @SerializedName("items") val items: List<T> = emptyList(),
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("hasMore") val isHasMore: Boolean,
    @SerializedName("totalPages") val totalPages: Int
)
