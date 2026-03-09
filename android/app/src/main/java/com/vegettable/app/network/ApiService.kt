package com.vegettable.app.network

import com.vegettable.app.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit API 介面 — 對應 .NET 後端所有端點
 */
interface ApiService {

    // ─── Products ────────────────────────────────────────────
    @GET("/api/products")
    fun getProducts(
        @Query("category") category: String?
    ): Call<ApiResponse<List<ProductSummary>>>

    @GET("/api/products/paginated")
    fun getProductsPaginated(
        @Query("category") category: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<ApiResponse<PaginatedResponse<ProductSummary>>>

    @GET("/api/products/search")
    fun searchProducts(
        @Query("keyword") keyword: String?
    ): Call<ApiResponse<List<ProductSummary>>>

    @GET("/api/products/search/paginated")
    fun searchProductsPaginated(
        @Query("keyword") keyword: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<ApiResponse<PaginatedResponse<ProductSummary>>>

    @GET("/api/products/{cropName}")
    fun getProductDetail(
        @Path("cropName") cropName: String
    ): Call<ApiResponse<ProductDetail>>

    // ─── Markets ─────────────────────────────────────────────
    @GET("/api/markets")
    fun getMarkets(): Call<ApiResponse<List<Market>>>

    @GET("/api/markets/{marketName}/prices")
    fun getMarketPrices(
        @Path("marketName") marketName: String,
        @Query("cropName") cropName: String?
    ): Call<ApiResponse<List<MarketPrice>>>

    @GET("/api/markets/compare/{cropName}")
    fun compareMarketPrices(
        @Path("cropName") cropName: String,
        @Query("markets") markets: String?
    ): Call<ApiResponse<List<MarketPrice>>>

    // ─── Alerts ──────────────────────────────────────────────
    @GET("/api/alerts")
    fun getAlerts(
        @Query("deviceToken") deviceToken: String?
    ): Call<ApiResponse<List<PriceAlert>>>

    @POST("/api/alerts")
    fun createAlert(
        @Body request: CreateAlertRequest
    ): Call<ApiResponse<PriceAlert>>

    @DELETE("/api/alerts/{id}")
    fun deleteAlert(
        @Path("id") id: Int,
        @Query("deviceToken") deviceToken: String?
    ): Call<Void>

    @PATCH("/api/alerts/{id}/toggle")
    fun toggleAlert(
        @Path("id") id: Int,
        @Query("deviceToken") deviceToken: String?
    ): Call<Void>

    // ─── Prediction / Seasonal / Recipes ─────────────────────
    @GET("/api/prediction/{cropName}")
    fun getPrediction(
        @Path("cropName") cropName: String
    ): Call<ApiResponse<PricePrediction>>

    @GET("/api/prediction/seasonal")
    fun getSeasonalInfo(
        @Query("category") category: String?
    ): Call<ApiResponse<List<SeasonalInfo>>>

    @GET("/api/prediction/{cropName}/recipes")
    fun getRecipes(
        @Path("cropName") cropName: String
    ): Call<ApiResponse<List<Recipe>>>

    // ─── Health ──────────────────────────────────────────────
    @GET("/health")
    fun healthCheck(): Call<Void>
}
