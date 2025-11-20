package com.game.internetshop.ui.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.internetshop.R
import com.game.internetshop.data.model.Product

class ProductAdapter(
    private val onIncreaseButtonClick: (productId: Int) -> Unit,
    private val onDecreaseButtonClick: (productId: Int) -> Unit,
    ): ListAdapter<CatalogueUiItem, ProductAdapter.ProductViewHolder>(ProductCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_test, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // логика view-шки карточки товара
    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.imageView)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvName)
        private val tvCounter: TextView = itemView.findViewById(R.id.tvCounter)
        private val buttonIncrease: Button = itemView.findViewById(R.id.buttonIncrease)
        private val buttonDecrease: Button = itemView.findViewById(R.id.buttonDecrease)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(catalogueUiItem: CatalogueUiItem) {
            val product: Product = catalogueUiItem.product
            val quantityInCart: Int = catalogueUiItem.quantityInCart

            ivProductImage.setImageResource(product.imageRes ?: R.drawable.ic_product)
            tvProductName.text = catalogueUiItem.product.name
            tvCounter.text = quantityInCart.toString()
            tvPrice.text = "${itemView.context.getString(R.string.price)} ${String.format("%.2f", catalogueUiItem.product.price)} ₽"

            buttonDecrease.isEnabled = quantityInCart > 0
            buttonDecrease.alpha = if (quantityInCart > 0) 1f else 0.75f

            buttonIncrease.setOnClickListener {
                onIncreaseButtonClick(product.id)
            }

            buttonDecrease.setOnClickListener {
                if (quantityInCart > 0) {
                    onDecreaseButtonClick(product.id)
                }
            }
        }
    }

    class ProductCallBack: DiffUtil.ItemCallback<CatalogueUiItem>() {
        override fun areItemsTheSame(oldItem: CatalogueUiItem, newItem: CatalogueUiItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CatalogueUiItem, newItem: CatalogueUiItem): Boolean {
            return oldItem == newItem
        }


    }
}