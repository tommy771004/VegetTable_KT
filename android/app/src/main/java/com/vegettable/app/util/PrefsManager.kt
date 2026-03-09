package com.vegettable.app.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * SharedPreferences 管理 — 設定、收藏、快取
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    companion object {
        private const val PREFS_NAME = "vegettable_prefs"
        private const val KEY_PRICE_UNIT = "price_unit"        // "kg" | "catty"
        private const val KEY_SHOW_RETAIL = "show_retail"
        private const val KEY_DARK_MODE = "dark_mode"          // "system" | "light" | "dark"
        private const val KEY_LANGUAGE = "language"            // "zh-TW" | "en" | "vi" | "id"
        private const val KEY_SELECTED_MARKET = "selected_market"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_CACHED_PRODUCTS = "cached_products"
        private const val KEY_CACHE_TIME = "cache_time"
    }

    // ─── Price Unit ──────────────────────────────────────────
    var priceUnit: String
        get() = prefs.getString(KEY_PRICE_UNIT, "kg") ?: "kg"
        set(unit) = prefs.edit().putString(KEY_PRICE_UNIT, unit).apply()

    var isShowRetailPrice: Boolean
        get() = prefs.getBoolean(KEY_SHOW_RETAIL, false)
        set(show) = prefs.edit().putBoolean(KEY_SHOW_RETAIL, show).apply()

    // ─── Dark Mode ───────────────────────────────────────────
    var darkMode: String
        get() = prefs.getString(KEY_DARK_MODE, "system") ?: "system"
        set(mode) = prefs.edit().putString(KEY_DARK_MODE, mode).apply()

    // ─── Language ────────────────────────────────────────────
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "zh-TW") ?: "zh-TW"
        set(lang) = prefs.edit().putString(KEY_LANGUAGE, lang).apply()

    // ─── Market ──────────────────────────────────────────────
    var selectedMarket: String?
        get() = prefs.getString(KEY_SELECTED_MARKET, null)
        set(market) = prefs.edit().putString(KEY_SELECTED_MARKET, market).apply()

    // ─── Favorites ───────────────────────────────────────────
    val favorites: Set<String>
        get() = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun isFavorite(cropCode: String): Boolean {
        return favorites.contains(cropCode)
    }

    fun toggleFavorite(cropCode: String) {
        val favs = favorites.toMutableSet()
        if (favs.contains(cropCode)) {
            favs.remove(cropCode)
        } else {
            favs.add(cropCode)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply()
    }

    // ─── Offline Cache ───────────────────────────────────────
    fun cacheProducts(json: String) {
        prefs.edit()
            .putString(KEY_CACHED_PRODUCTS, json)
            .putLong(KEY_CACHE_TIME, System.currentTimeMillis())
            .apply()
    }

    val cachedProducts: String?
        get() = prefs.getString(KEY_CACHED_PRODUCTS, null)

    val isCacheStale: Boolean
        get() {
            val cacheTime = prefs.getLong(KEY_CACHE_TIME, 0)
            val thirtyMinutes = 30 * 60 * 1000L
            return (System.currentTimeMillis() - cacheTime) > thirtyMinutes
        }

    /** 載入有效快取（未過期） */
    val validCachedProducts: String?
        get() {
            if (isCacheStale) return null
            return cachedProducts
        }

    /** 快取時間描述 */
    val cacheAge: String
        get() {
            val cacheTime = prefs.getLong(KEY_CACHE_TIME, 0)
            if (cacheTime == 0L) return ""
            val minutes = (System.currentTimeMillis() - cacheTime) / 60000
            if (minutes < 1) return "剛剛更新"
            if (minutes < 60) return "$minutes 分鐘前更新"
            return "${minutes / 60} 小時前更新"
        }
}
