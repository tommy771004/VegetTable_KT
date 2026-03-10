package com.vegettable.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 本機快取 — 產品摘要
 * 完全以 Kotlin data class 撰寫（無 Java）
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "crop_code")
    val cropCode: String,

    @ColumnInfo(name = "crop_name")
    val cropName: String,

    @ColumnInfo(name = "avg_price")
    val avgPrice: Double,

    @ColumnInfo(name = "prev_avg_price")
    val prevAvgPrice: Double,

    @ColumnInfo(name = "historical_avg_price")
    val historicalAvgPrice: Double,

    @ColumnInfo(name = "volume")
    val volume: Double,

    @ColumnInfo(name = "price_level")
    val priceLevel: String?,

    @ColumnInfo(name = "trend")
    val trend: String?,

    @ColumnInfo(name = "category")
    val category: String?,

    @ColumnInfo(name = "sub_category")
    val subCategory: String?,

    /** JSON 序列化的 aliases 清單 */
    @ColumnInfo(name = "aliases_json")
    val aliasesJson: String?,

    /** JSON 序列化的 recentPrices 清單 */
    @ColumnInfo(name = "recent_prices_json")
    val recentPricesJson: String?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
