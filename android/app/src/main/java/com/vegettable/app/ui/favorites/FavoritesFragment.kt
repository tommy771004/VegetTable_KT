package com.vegettable.app.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class FavoritesFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvFavCount: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: ProductAdapter
    private lateinit var prefs: PrefsManager
    private var currentCall: Call<ApiResponse<List<ProductSummary>>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        rvFavorites = view.findViewById(R.id.rv_favorites)
        tvEmpty = view.findViewById(R.id.tv_empty)
        tvFavCount = view.findViewById(R.id.tv_fav_count)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(this, prefs.favorites)
        adapter.setPriceUnit(prefs.priceUnit)
        adapter.setShowRetail(prefs.isShowRetailPrice)
        rvFavorites.adapter = adapter

        swipeRefresh.setColorSchemeResources(android.R.color.holo_green_dark)
        swipeRefresh.setOnRefreshListener { loadFavorites() }

        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        adapter.setPriceUnit(prefs.priceUnit)
        adapter.setShowRetail(prefs.isShowRetailPrice)
        adapter.setFavorites(prefs.favorites)
        loadFavorites()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            adapter.setPriceUnit(prefs.priceUnit)
            adapter.setShowRetail(prefs.isShowRetailPrice)
            adapter.setFavorites(prefs.favorites)
            loadFavorites()
        }
    }

    private fun loadFavorites() {
        currentCall?.cancel()
        
        val favCodes = prefs.favorites

        if (favCodes.isEmpty()) {
            swipeRefresh.isRefreshing = false
            tvEmpty.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
            tvFavCount.text = "0 項收藏"
            return
        }

        tvFavCount.text = "${favCodes.size} 項收藏"

        // 從快取中過濾收藏項目
        val cached = prefs.cachedProducts
        if (cached != null) {
            try {
                val type = object : TypeToken<List<ProductSummary>>() {}.type
                val all: List<ProductSummary>? = Gson().fromJson(cached, type)
                
                if (all != null) {
                    val favProducts = all.filter { favCodes.contains(it.cropCode) }
                    if (favProducts.isNotEmpty()) {
                        swipeRefresh.isRefreshing = false
                        adapter.setItems(favProducts)
                        rvFavorites.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                        return
                    }
                }
            } catch (ignored: Exception) {
            }
        }

        // 若快取為空，從 API 載入全部產品再過濾
        currentCall = ApiClient.getInstance().api.getProducts(null)
        currentCall?.enqueue(object : Callback<ApiResponse<List<ProductSummary>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<ProductSummary>>>,
                    response: Response<ApiResponse<List<ProductSummary>>>
                ) {
                    if (!isAdded) return
                    swipeRefresh.isRefreshing = false

                    val body = response.body()
                    if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                        val favProducts = body.data.filter { favCodes.contains(it.cropCode) }
                        adapter.setItems(favProducts)
                        rvFavorites.visibility = if (favProducts.isEmpty()) View.GONE else View.VISIBLE
                        tvEmpty.visibility = if (favProducts.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<ProductSummary>>>, t: Throwable) {
                    if (!isAdded) return
                    swipeRefresh.isRefreshing = false
                    tvEmpty.visibility = View.VISIBLE
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
        loadFavorites()
    override fun onDestroyView() {
        super.onDestroyView()
        currentCall?.cancel()
    }
}
