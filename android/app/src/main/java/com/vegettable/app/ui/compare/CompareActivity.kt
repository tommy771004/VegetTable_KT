package com.vegettable.app.ui.compare

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vegettable.app.R
import com.vegettable.app.model.ApiResponse
import com.vegettable.app.model.MarketPrice
import com.vegettable.app.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompareActivity : AppCompatActivity() {

    private lateinit var cropName: String
    private lateinit var rvCompare: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CompareAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        cropName = intent.getStringExtra("cropName") ?: ""

        if (cropName.isEmpty()) {
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "$cropName - 市場價格比較"

        toolbar.setNavigationOnClickListener { onBackPressed() }

        rvCompare = findViewById(R.id.rv_compare)
        progressBar = findViewById(R.id.progress_bar)

        rvCompare.layoutManager = LinearLayoutManager(this)
        adapter = CompareAdapter()
        rvCompare.adapter = adapter

        loadCompareData()
    }

    private fun loadCompareData() {
        progressBar.visibility = View.VISIBLE
        ApiClient.getInstance().api.compareMarketPrices(cropName, null)
            .enqueue(object : Callback<ApiResponse<List<MarketPrice>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<MarketPrice>>>,
                    response: Response<ApiResponse<List<MarketPrice>>>
                ) {
                    progressBar.visibility = View.GONE
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                        adapter.setItems(body.data)
                    } else {
                        Toast.makeText(this@CompareActivity, "無法取得比較資料", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<MarketPrice>>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CompareActivity, "網路錯誤: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
