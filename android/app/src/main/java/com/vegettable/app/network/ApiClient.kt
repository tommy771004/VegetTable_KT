package com.vegettable.app.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.vegettable.app.BuildConfig
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * Retrofit 單例客戶端（含指數退避重試）
 */
class ApiClient private constructor() {

    val api: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // 指數退避重試攔截器（修正 response 洩漏問題）
        val retryInterceptor = Interceptor { chain ->
            val request = chain.request()
            var response: Response? = null
            var lastException: IOException? = null

            for (attempt in 0..MAX_RETRIES) {
                try {
                    // 在重試前關閉上一次的 response body，避免連線洩漏
                    response?.close()
                    response = null
                    
                    response = chain.proceed(request)
                    if (response.isSuccessful) return@Interceptor response
                    // 4xx 不重試（用戶端錯誤，重試無意義）
                    if (response.code in 400..499) return@Interceptor response
                    // 504 (Only-if-cached 失敗) 不重試，避免離線時無謂等待
                    if (response.code == 504) return@Interceptor response
                    // 其他 5xx 會重試，先關閉此次 response
                } catch (e: IOException) {
                    lastException = e
                }
                
                if (attempt < MAX_RETRIES) {
                    try {
                        val delay = (1000 * 2.0.pow(attempt.toDouble())).toLong()
                        Thread.sleep(delay)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        response?.close()
                        throw IOException("重試被中斷", e)
                    }
                }
            }
            response ?: throw lastException ?: IOException("重試失敗")
        }

        // 離線 fallback 攔截器 — 無網路時強制使用快取
        val offlineFallbackInterceptor = Interceptor { chain ->
            var req = chain.request()
            if (appContext != null && !isNetworkAvailable()) {
                req = req.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            chain.proceed(req)
        }

        // 網路攔截器 — 強制寫入 Cache-Control 標頭，確保 OkHttp 會快取回應
        val networkCacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.HOURS) // 快取 1 小時
                .build()
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        val httpBuilder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(offlineFallbackInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(logging)
            .addNetworkInterceptor(networkCacheInterceptor)

        // OkHttp 磁碟快取 (10 MB)
        appContext?.let { context ->
            val cacheDir = File(context.cacheDir, "http_cache")
            httpBuilder.cache(Cache(cacheDir, CACHE_SIZE))
        }

        val client = httpBuilder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    companion object {
        @Volatile
        private var instance: ApiClient? = null
        private var appContext: Application? = null

        private const val MAX_RETRIES = 3
        private const val CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

        @JvmStatic
        fun init(app: Application) {
            appContext = app
        }

        @JvmStatic
        fun getInstance(): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient().also { instance = it }
            }
        }

        private fun isNetworkAvailable(): Boolean {
            val context = appContext ?: return true
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
            val caps = cm.getNetworkCapabilities(cm.activeNetwork)
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        }
    }
}
