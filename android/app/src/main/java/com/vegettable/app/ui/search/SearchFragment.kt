package com.vegettable.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.vegettable.app.R
import com.vegettable.app.model.ApiResponse
import com.vegettable.app.model.ProductSummary
import com.vegettable.app.network.ApiClient
import com.vegettable.app.ui.adapter.ProductAdapter
import com.vegettable.app.ui.detail.DetailActivity
import com.vegettable.app.util.PrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var etSearch: TextInputEditText
    private lateinit var rvResults: RecyclerView
    private lateinit var tvResultCount: TextView
    private lateinit var tvSearchError: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: ProductAdapter
    private lateinit var prefs: PrefsManager
    
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var currentSearchCall: Call<ApiResponse<List<ProductSummary>>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        etSearch = view.findViewById(R.id.et_search)
        rvResults = view.findViewById(R.id.rv_results)
        tvResultCount = view.findViewById(R.id.tv_result_count)
        tvSearchError = view.findViewById(R.id.tv_search_error)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        rvResults.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(this, prefs.favorites)
        adapter.setPriceUnit(prefs.priceUnit)
        adapter.setShowRetail(prefs.isShowRetailPrice)
        rvResults.adapter = adapter

        swipeRefresh.setColorSchemeResources(R.color.primary)
        swipeRefresh.setOnRefreshListener {
            val kw = etSearch.text?.toString()?.trim() ?: ""
            if (kw.isNotEmpty()) {
                performSearch(kw)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        // 搜尋防抖
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { performSearch(s?.toString()?.trim() ?: "") }
                handler.postDelayed(searchRunnable!!, 300)
            }
        })
    }

    private fun performSearch(keyword: String) {
        // 取消前一次尚未完成的搜尋請求
        currentSearchCall?.cancel()
        currentSearchCall = null

        if (keyword.isEmpty()) {
            adapter.setItems(emptyList())
            tvResultCount.visibility = View.GONE
            return
        }

        tvSearchError.visibility = View.GONE

        currentSearchCall = ApiClient.getInstance().api.searchProducts(keyword)
        currentSearchCall?.enqueue(object : Callback<ApiResponse<List<ProductSummary>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<ProductSummary>>>,
                response: Response<ApiResponse<List<ProductSummary>>>
            ) {
                if (!isAdded) return
                swipeRefresh.isRefreshing = false
                
                val body = response.body()
                if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                    val results = body.data
                    adapter.setItems(results)
                    tvResultCount.text = "找到 ${results.size} 項結果"
                    tvResultCount.visibility = View.VISIBLE
                    tvSearchError.visibility = View.GONE
                } else {
                    adapter.setItems(emptyList())
                    tvResultCount.visibility = View.GONE
                    tvSearchError.text = "搜尋失敗"
                    tvSearchError.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<ProductSummary>>>, t: Throwable) {
                if (call.isCanceled) return
                if (!isAdded) return
                swipeRefresh.isRefreshing = false
                adapter.setItems(emptyList())
                tvResultCount.visibility = View.GONE
                tvSearchError.text = "搜尋失敗: ${t.message}"
                tvSearchError.visibility = View.VISIBLE
            }
        })
    }

    override fun onItemClick(product: ProductSummary) {
        // 暫時註解掉跳轉，避免進入尚未實作的空白畫面
        // val intent = Intent(requireContext(), DetailActivity::class.java).apply {
        //     putExtra("cropName", product.cropName)
        //     putExtra("cropCode", product.cropCode)
        // }
        // startActivity(intent)
        android.widget.Toast.makeText(requireContext(), "商品詳情功能開發中", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onFavoriteClick(product: ProductSummary) {
        prefs.toggleFavorite(product.cropCode)
        adapter.setFavorites(prefs.favorites)
    }

    override fun onResume() {
        super.onResume()
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
        searchRunnable?.let { handler.removeCallbacks(it) }
        currentSearchCall?.cancel()
    }
}
