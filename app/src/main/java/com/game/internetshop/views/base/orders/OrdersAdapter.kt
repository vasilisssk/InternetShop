package com.game.internetshop.views.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.game.internetshop.R
import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInOrder
import com.game.internetshop.viewmodels.CartViewModel

class OrdersAdapter(): ListAdapter<OrdersUiItem, OrdersAdapter.OrdersViewHolder>(OrdersAdapter.OrderCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_card, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrdersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvInfoAboutOrder: TextView = itemView.findViewById(R.id.tvOrderTitle)
        private val tvDayOfOrder: TextView = itemView.findViewById(R.id.tvDateOfOrderValue)
        private val tvPaymentVariant: TextView = itemView.findViewById(R.id.tvPaymentVariantValue)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatusValue)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPriceValue)
        private val tvOrderedItems: TextView = itemView.findViewById(R.id.tvOrderedItems)

        fun bind(ordersUiItem: OrdersUiItem) {
            val order: Order = ordersUiItem.order
            val productsInOrder: List<ProductInOrder> = ordersUiItem.productsInOrder
            val orderedProducts: List<Product> = ordersUiItem.products
            val context = itemView.context

            val info: String = getString(context, R.string.info_about_order)  + " " + order.orderId.toString()
            tvInfoAboutOrder.text = info
            tvDayOfOrder.text = order.registrationDate.toString().replace("T", " ").replace("-",".")
            val arrayOfPaymentMethods =  context.resources.getStringArray(R.array.payment_methods)
            tvPaymentVariant.text = arrayOfPaymentMethods[CartViewModel.PaymentMethod.getPaymentName(order.paymentId).code]
            tvOrderStatus.text = when (order.statusId) {
                1 -> context.getString(R.string.order_status_in_processing)
                2 -> context.getString(R.string.order_status_confirmed)
                3 -> context.getString(R.string.order_status_is_being_collected)
                4 -> context.getString(R.string.order_status_ready_for_shipment)
                5 -> context.getString(R.string.order_status_shipped)
                6 -> context.getString(R.string.order_status_with_the_courier)
                7 -> context.getString(R.string.order_status_delivered)
                8 -> context.getString(R.string.order_status_canceled)
                9 -> context.getString(R.string.order_status_refund)
                else -> context.getString(R.string.order_status_at_the_pick_up_point)
            }
            tvTotalPrice.text = "${String.format("%.2f", order.totalPrice)} ₽"

            val productsInfo = productsInOrder.map {
                productInOrder -> ProductInfo(
                    orderedProducts.find { it.id == productInOrder.productId}?.name ?: "",
                orderedProducts.find {it.id == productInOrder.productId}?.brand ?: "",
                productInOrder.quantity,
                orderedProducts.find { it.id == productInOrder.productId}?.price ?: 0f
                )
            }

            val orderedProductsInfo: StringBuilder = StringBuilder("")
            for (productInfo in productsInfo) {
                orderedProductsInfo.append("${productInfo.name} (${productInfo.brand}) - ${productInfo.quantity} ${context.getString(R.string.piece)} - ${productInfo.price} ₽ \n")
            }

            tvOrderedItems.text = orderedProductsInfo.toString()
        }
    }

    class OrderCallBack: DiffUtil.ItemCallback<OrdersUiItem>() {
        override fun areItemsTheSame(
            oldItem: OrdersUiItem,
            newItem: OrdersUiItem
        ): Boolean {
            return oldItem.order.orderId == newItem.order.orderId
        }

        override fun areContentsTheSame(
            oldItem: OrdersUiItem,
            newItem: OrdersUiItem
        ): Boolean {
            return oldItem.order == newItem.order
        }

    }
}