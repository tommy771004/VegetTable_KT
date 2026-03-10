package com.vegettable.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vegettable.app.R
import com.vegettable.app.model.ApiResponse
import com.vegettable.app.model.PaginatedResponse
import com.vegettable.app.model.ProductSummary
import com.vegettable.app.network.ApiClient
import com.vegettable.app.ui.adapter.ProductAdapter
import com.vegettable.app.ui.detail.DetailActivity
import com.vegettable.app.util.PrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var rvProducts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutError: LinearLayout
    private lateinit var tvError: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var chipGroupCategory: ChipGroup

    private lateinit var adapter: ProductAdapter
    private lateinit var prefs: PrefsManager
    private var selectedCategory: String? = null

    // 分頁狀態
    private var currentOffset = 0
    private var hasMore = false
    private var isLoadingMore = false
    private var currentCall: Call<ApiResponse<PaginatedResponse<ProductSummary>>>? = null

    companion object {
        private const val PAGE_SIZE = 20
        private val CATEGORIES = arrayOf(
            arrayOf("all", "全部"),
            arrayOf("vegetable", "蔬菜"),
            arrayOf("fruit", "水果"),
            arrayOf("fish", "漁產"),
            arrayOf("poultry", "肉品"),
            arrayOf("flower", "花卉"),
            arrayOf("rice", "白米")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        rvProducts = view.findViewById(R.id.rv_products)
        progressBar = view.findViewById(R.id.progress_bar)
        layoutError = view.findViewById(R.id.layout_error)
        tvError = view.findViewById(R.id.tv_error)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        chipGroupCategory = view.findViewById(R.id.chip_group_category)

        // 設定 RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        rvProducts.layoutManager = layoutManager
        adapter = ProductAdapter(this, prefs.favorites)
        adapter.setPriceUnit(prefs.priceUnit)
        adapter.setShowRetail(prefs.isShowRetailPrice)
        rvProducts.adapter = adapter

        // Infinite scroll — 接近底部時自動載入下一頁
        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return // 只在向下滾動時觸發
                val visibleCount = layoutManager.childCount
                val totalCount = layoutManager.itemCount
                val firstVisible = layoutManager.findFirstVisibleItemPosition()

                if (hasMore && !isLoadingMore && (visibleCount + firstVisible + 5) >= totalCount) {
                    loadMoreProducts()
                }
            }
        })

        // 分類 Chips
        setupCategoryChips()

        // 下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.primary)
        swipeRefresh.setOnRefreshListener { loadProducts() }

        // 重試按鈕
        view.findViewById<View>(R.id.btn_retry).setOnClickListener { loadProducts() }

        // 載入資料
        loadProducts()
    }

    private fun setupCategoryChips() {
        for (cat in CATEGORIES) {
            val chip = Chip(requireContext()).apply {
                text = cat[1]
                isCheckable = true
                tag = cat[0]
                if ("all" == cat[0]) {
                    isChecked = true
                }
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        val tag = buttonView.tag as String
                        selectedCategory = if ("all" == tag) null else tag
                        loadProducts()
                    }
                }
            }
            chipGroupCategory.addView(chip)
        }
    }

    private fun loadProducts() {
        currentCall?.cancel()
        
        progressBar.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
        currentOffset = 0

        currentCall = ApiClient.getInstance().api.getProductsPaginated(selectedCategory, 0, PAGE_SIZE)
        currentCall?.enqueue(object : Callback<ApiResponse<PaginatedResponse<ProductSummary>>> {
                override fun onResponse(
                    call: Call<ApiResponse<PaginatedResponse<ProductSummary>>>,
                    response: Response<ApiResponse<PaginatedResponse<ProductSummary>>>
                ) {
                    if (!isAdded) return
                    swipeRefresh.isRefreshing = false
                    progressBar.visibility = View.GONE

                    val body = response.body()
                    if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                        val page = body.data
                        val products = page.items
                        adapter.setItems(products)
                        hasMore = page.isHasMore
                        currentOffset = products.size
                        rvProducts.visibility = View.VISIBLE

                        // 快取
                        prefs.cacheProducts(Gson().toJson(products))
                    } else {
                        showError("無法取得資料")
                        loadCachedProducts()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<PaginatedResponse<ProductSummary>>>,
                    t: Throwable
                ) {
                    if (call.isCanceled) return
                    if (!isAdded) return
                    swipeRefresh.isRefreshing = false
                    progressBar.visibility = View.GONE
                    showError("網路錯誤: ${t.message}")
                    loadCachedProducts()
                }
            })
    }

    private fun loadMoreProducts() {
        if (!hasMore || isLoadingMore) return
        isLoadingMore = true

        currentCall = ApiClient.getInstance().api.getProductsPaginated(selectedCategory, currentOffset, PAGE_SIZE)
        currentCall?.enqueue(object : Callback<ApiResponse<PaginatedResponse<ProductSummary>>> {
                override fun onResponse(
                    call: Call<ApiResponse<PaginatedResponse<ProductSummary>>>,
                    response: Response<ApiResponse<PaginatedResponse<ProductSummary>>>
                ) {
                    isLoadingMore = false
                    if (!isAdded) return

                    val body = response.body()
                    if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                        val page = body.data
                        val newItems = page.items
                        adapter.addItems(newItems)
                        hasMore = page.isHasMore
                        currentOffset += newItems.size
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<PaginatedResponse<ProductSummary>>>,
                    t: Throwable
                ) {
                    isLoadingMore = false
                }
            })
    }

    private fun loadCachedProducts() {
        val cached = prefs.cachedProducts
        if (cached != null) {
            try {
                val type = object : TypeToken<List<ProductSummary>>() {}.type
                val products: List<ProductSummary>? = Gson().fromJson(cached, type)
                if (!products.isNullOrEmpty()) {
                    adapter.setItems(products)
                    rvProducts.visibility = View.VISIBLE
                    layoutError.visibility = View.GONE
                    hasMore = false // 離線快取不支援分頁載入
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Failed to parse cached products", e)
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        layoutError.visibility = View.VISIBLE
    }

    override fun onItemClick(product: ProductSummary) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("cropName", product.cropName)
            putExtra("cropCode", product.cropCode)
        }
        startActivity(intent)
    }

    override fun onFavoriteClick(product: ProductSummary) {
        prefs.toggleFavorite(product.cropCode)
        // 更新收藏狀態
        adapter.setFavorites(prefs.favorites)
    }

    override fun onResume() {
        super.onResume()
        // 更新收藏狀態和設定
        adapter.setPriceUnit(prefs.priceUnit)
        adapter.setShowRetail(prefs.isShowRetailPrice)
        adapter.setFavorites(prefs.favorites)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            adapter.setPriceUnit(prefs.priceUnit)
            adapter.setShowRetail(prefs.isShowRetailPrice)
            adapter.setFavorites(prefs.favorites)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentCall?.cancel()
    }
}
