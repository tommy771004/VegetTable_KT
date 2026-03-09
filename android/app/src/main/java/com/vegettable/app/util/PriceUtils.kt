package com.vegettable.app.util

import android.graphics.Color

/**
 * 價格相關工具 — Liquid Glass 配色
 */
object PriceUtils {

    /** 公斤 → 台斤 換算比 */
    private const val KG_TO_CATTY = 0.6

    /** 零售估算倍率 */
    private const val RETAIL_MULTIPLIER = 2.5

    @JvmStatic
    fun convertToCatty(kgPrice: Double): Double {
        return kgPrice * KG_TO_CATTY
    }

    @JvmStatic
    fun estimateRetailPrice(wholesalePrice: Double): Double {
        return wholesalePrice * RETAIL_MULTIPLIER
    }

    @JvmStatic
    fun formatPrice(price: Double): String {
        return if (price == price.toInt().toDouble()) {
            price.toInt().toString()
        } else {
            String.format("%.1f", price)
        }
    }

    /** 依 priceLevel 取得對應顏色 */
    @JvmStatic
    fun getPriceLevelColor(level: String?): Int {
        return when (level) {
            "very-cheap" -> Color.parseColor("#E53935")
            "cheap" -> Color.parseColor("#FF7043")
            "normal" -> Color.parseColor("#42A5F5")
            "expensive" -> Color.parseColor("#1565C0")
            else -> Color.GRAY
        }
    }

    /** 依 priceLevel 取得背景顏色 (帶透明度) */
    @JvmStatic
    fun getPriceLevelBgColor(level: String?): Int {
        return when (level) {
            "very-cheap" -> Color.parseColor("#1EE53935")
            "cheap" -> Color.parseColor("#1EFF7043")
            "normal" -> Color.parseColor("#1E42A5F5")
            "expensive" -> Color.parseColor("#1E1565C0")
            else -> Color.parseColor("#10808080")
        }
    }

    /** 價格等級 → 中文標籤 */
    @JvmStatic
    fun getPriceLevelLabel(level: String?): String {
        return when (level) {
            "very-cheap" -> "當令便宜"
            "cheap" -> "相對便宜"
            "normal" -> "略偏貴"
            "expensive" -> "相對偏貴"
            else -> ""
        }
    }

    /** 趨勢 → 箭頭符號 */
    @JvmStatic
    fun getTrendArrow(trend: String?): String {
        return when (trend) {
            "up" -> "↑"
            "down" -> "↓"
            else -> "→"
        }
    }

    /** 趨勢 → 顏色 */
    @JvmStatic
    fun getTrendColor(trend: String?): Int {
        return when (trend) {
            "up" -> Color.parseColor("#E53935")
            "down" -> Color.parseColor("#2E7D32")
            else -> Color.parseColor("#757575")
        }
    }
}
