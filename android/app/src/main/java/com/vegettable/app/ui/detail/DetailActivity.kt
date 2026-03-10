package com.vegettable.app.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.vegettable.app.R
import com.vegettable.app.model.ApiResponse
import com.vegettable.app.model.ProductDetail
import com.vegettable.app.network.ApiClient
import com.vegettable.app.ui.compare.CompareActivity
import com.vegettable.app.util.PriceUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var cropName: String
    private lateinit var cropCode: String

    private lateinit var ivProduct: ImageView
    private lateinit var tvCropName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvCurrentPrice: TextView
    private lateinit var tvPriceTrend: TextView
    private lateinit var tvHighestPrice: TextView
    private lateinit var tvLowestPrice: TextView
    private lateinit var tvVolume: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnCompare: Button
    private lateinit var btnPrediction: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        cropName = intent.getStringExtra("cropName") ?: ""
        cropCode = intent.getStringExtra("cropCode") ?: ""

        if (cropName.isEmpty()) {
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = cropName

        toolbar.setNavigationOnClickListener { onBackPressed() }

        ivProduct = findViewById(R.id.iv_product)
        tvCropName = findViewById(R.id.tv_crop_name)
        tvCategory = findViewById(R.id.tv_category)
        tvCurrentPrice = findViewById(R.id.tv_current_price)
        tvPriceTrend = findViewById(R.id.tv_price_trend)
        tvHighestPrice = findViewById(R.id.tv_highest_price)
        tvLowestPrice = findViewById(R.id.tv_lowest_price)
        tvVolume = findViewById(R.id.tv_volume)
        tvDescription = findViewById(R.id.tv_description)
        btnCompare = findViewById(R.id.btn_compare)
        btnPrediction = findViewById(R.id.btn_prediction)
        progressBar = findViewById(R.id.progress_bar)

        btnCompare.setOnClickListener {
            val intent = Intent(this, CompareActivity::class.java).apply {
                putExtra("cropName", cropName)
            }
            startActivity(intent)
        }

        btnPrediction.setOnClickListener {
            Toast.makeText(this, "預測功能開發中", Toast.LENGTH_SHORT).show()
        }

        loadDetail()
    }

    private fun loadDetail() {
        progressBar.visibility = View.VISIBLE
        ApiClient.getInstance().api.getProductDetail(cropName)
            .enqueue(object : Callback<ApiResponse<ProductDetail>> {
                override fun onResponse(
                    call: Call<ApiResponse<ProductDetail>>,
                    response: Response<ApiResponse<ProductDetail>>
                ) {
                    progressBar.visibility = View.GONE
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.isSuccess && body.data != null) {
                        bindData(body.data)
                    } else {
                        Toast.makeText(this@DetailActivity, "無法取得詳細資料", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<ProductDetail>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@DetailActivity, "網路錯誤: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun bindData(detail: ProductDetail) {
        tvCropName.text = detail.cropName
        tvCategory.text = detail.category
        tvCurrentPrice.text = PriceUtils.formatPrice(detail.currentPrice, "kg")
        
        tvHighestPrice.text = PriceUtils.formatPrice(detail.highestPrice, "kg")
        tvLowestPrice.text = PriceUtils.formatPrice(detail.lowestPrice, "kg")
        tvVolume.text = "${detail.volume.toInt()} 公斤"
        tvDescription.text = detail.description

        if (detail.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(detail.imageUrl)
                .centerCrop()
                .into(ivProduct)
        }
    }
}
