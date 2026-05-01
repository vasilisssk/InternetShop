package com.game.internetshop.views.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.internetshop.R
import com.game.internetshop.data.model.Product
import com.game.internetshop.views.catalogue.CatalogueUiItem
import com.game.internetshop.views.catalogue.ProductAdapter

class ProductAdapterCart(
    private val onIncreaseButtonClick: (productId: Int) -> Unit,
    private val onDecreaseButtonClick: (productId: Int) -> Unit
): ListAdapter<CatalogueUiItem, ProductAdapterCart.ProductInCartViewHolder>(ProductAdapter.ProductCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_card_cart, parent, false)
        return ProductInCartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductInCartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductInCartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvBrandValue: TextView = itemView.findViewById(R.id.tvBrandValue)
        private val tvPriceValue: TextView = itemView.findViewById(R.id.tvPriceValue)
        private val btnIncrease: Button = itemView.findViewById(R.id.btnIncreaseCart)
        private val btnDecrease: Button = itemView.findViewById(R.id.btnDecreaseCart)
        private val tvCounterCart: TextView = itemView.findViewById(R.id.tvCounterCart)
        fun bind(catalogueUiItem: CatalogueUiItem) {
            val product: Product = catalogueUiItem.product
            val quantityInCart: Int = catalogueUiItem.quantityInCart

            tvProductName.text = product.name
            tvBrandValue.text = product.brand
            tvPriceValue.text = "${String.format("%.2f", catalogueUiItem.product.price)} ₽"
            tvCounterCart.text = quantityInCart.toString()

            btnIncrease.setOnClickListener {
                onIncreaseButtonClick(product.id)
            }

            btnDecrease.isEnabled = quantityInCart > 0
            btnDecrease.alpha = if (quantityInCart > 0) 1f else 0.75f
            btnDecrease.setOnClickListener {
                if (quantityInCart > 0) {
                    onDecreaseButtonClick(product.id)
                }
            }
        }

    }
}