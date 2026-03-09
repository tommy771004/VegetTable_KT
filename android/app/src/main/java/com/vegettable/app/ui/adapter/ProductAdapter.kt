package com.vegettable.app.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vegettable.app.R
import com.vegettable.app.model.ProductSummary
import com.vegettable.app.util.PriceUtils

class ProductAdapter(
    private val listener: OnItemClickListener,
    private var favorites: Set<String>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: ProductSummary)
        fun onFavoriteClick(product: ProductSummary)
    }

    private var items: List<ProductSummary> = ArrayList()
    private var priceUnit: String = "kg"
    private var showRetail: Boolean = false

    fun setFavorites(newFavorites: Set<String>) {
        this.favorites = newFavorites
        notifyItemRangeChanged(0, items.size)
    }

    fun setItems(newItems: List<ProductSummary>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                return items[oldPos].cropCode == newItems[newPos].cropCode
            }

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                val o = items[oldPos]
                val n = newItems[newPos]
                return o.cropCode == n.cropCode &&
                        o.avgPrice == n.avgPrice &&
                        o.priceLevel == n.priceLevel &&
                        o.trend == n.trend
            }
        })
        this.items = newItems
        result.dispatchUpdatesTo(this)
    }

    fun addItems(newItems: List<ProductSummary>) {
        val start = items.size
        val mutableItems = items.toMutableList()
        mutableItems.addAll(newItems)
        items = mutableItems
        notifyItemRangeInserted(start, newItems.size)
    }

    fun setPriceUnit(unit: String) {
        if (this.priceUnit != unit) {
            this.priceUnit = unit
            notifyItemRangeChanged(0, items.size)
        }
    }

    fun setShowRetail(showRetail: Boolean) {
        if (this.showRetail != showRetail) {
            this.showRetail = showRetail
            notifyItemRangeChanged(0, items.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_crop_name)
        private val tvAliases: TextView = itemView.findViewById(R.id.tv_aliases)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvTrend: TextView = itemView.findViewById(R.id.tv_trend)
        private val tvUnit: TextView = itemView.findViewById(R.id.tv_unit)
        private val tvLevel: TextView = itemView.findViewById(R.id.tv_price_level)
        private val btnFav: ImageButton = itemView.findViewById(R.id.btn_favorite)

        fun bind(p: ProductSummary) {
            tvName.text = p.cropName

            // 別名
            if (!p.aliases.isNullOrEmpty()) {
                tvAliases.text = p.aliases.joinToString("、")
                tvAliases.visibility = View.VISIBLE
            } else {
                tvAliases.visibility = View.GONE
            }

            // 價格
            var displayPrice = p.avgPrice
            if ("catty" == priceUnit) {
                displayPrice = PriceUtils.convertToCatty(displayPrice)
            }
            if (showRetail) {
                displayPrice = PriceUtils.estimateRetailPrice(displayPrice)
            }
            tvPrice.text = PriceUtils.formatPrice(displayPrice)
            tvPrice.setTextColor(PriceUtils.getPriceLevelColor(p.priceLevel))

            // 趨勢
            tvTrend.text = PriceUtils.getTrendArrow(p.trend)
            tvTrend.setTextColor(PriceUtils.getTrendColor(p.trend))

            // 單位
            tvUnit.text = if ("catty" == priceUnit) "元/台斤" else "元/公斤"

            // 價格等級
            val levelLabel = PriceUtils.getPriceLevelLabel(p.priceLevel)
            tvLevel.text = levelLabel
            tvLevel.setTextColor(PriceUtils.getPriceLevelColor(p.priceLevel))
            
            val bg = GradientDrawable().apply {
                setColor(PriceUtils.getPriceLevelBgColor(p.priceLevel))
                cornerRadius = 20f
            }
            tvLevel.background = bg

            // 收藏
            val isFav = favorites.contains(p.cropCode)
            btnFav.setImageResource(
                if (isFav) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
            btnFav.contentDescription = if (isFav) "取消收藏" else "加入收藏"
            btnFav.setOnClickListener { listener.onFavoriteClick(p) }

            // TalkBack 無障礙
            itemView.contentDescription = "${p.cropName}，價格 ${PriceUtils.formatPrice(displayPrice)} 元，$levelLabel"

            // 點擊
            itemView.setOnClickListener { listener.onItemClick(p) }
        }
    }
}
