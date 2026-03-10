package com.vegettable.app.ui.compare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vegettable.app.R
import com.vegettable.app.model.MarketPrice
import com.vegettable.app.util.PriceUtils

class CompareAdapter : RecyclerView.Adapter<CompareAdapter.ViewHolder>() {

    private var items: List<MarketPrice> = emptyList()

    fun setItems(newItems: List<MarketPrice>) {
        items = newItems.sortedBy { it.price } // 價格由低到高排序
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compare, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRank: TextView = itemView.findViewById(R.id.tv_rank)
        private val tvMarketName: TextView = itemView.findViewById(R.id.tv_market_name)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvVolume: TextView = itemView.findViewById(R.id.tv_volume)

        fun bind(item: MarketPrice, position: Int) {
            tvRank.text = "${position + 1}"
            tvMarketName.text = item.marketName
            tvPrice.text = PriceUtils.formatPrice(item.price, "kg")
            tvVolume.text = "交易量: ${item.volume.toInt()} 公斤"
        }
    }
}
