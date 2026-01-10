package com.game.internetshop.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.internetshop.R
import com.game.internetshop.databinding.FragmentCartBinding
import com.game.internetshop.databinding.FragmentCatalogueBinding
import com.game.internetshop.ui.catalogue.CatalogueUiItem
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils

class CartFragment : Fragment() {
    private val viewModel: CartViewModel by viewModel()
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCartAdapter: ProductAdapterCart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageBtnMakeOrder.setOnClickListener {
            viewModel.onCreateOrderClicked()
        }

        setupRecycleView()
        observeViewModel()
        setupSpinner()
    }

    private fun setupRecycleView() {
        val rv: RecyclerView = binding.rvCart
        val linearLayoutManager = LinearLayoutManager(requireContext())

        myCartAdapter = ProductAdapterCart(
            onIncreaseButtonClick = { productId ->
                viewModel.onAddingToCart(productId)
            },
            onDecreaseButtonClick = { productId ->
                viewModel.onRemovingFromCart(productId)
            }
        )

        rv.apply {
            layoutManager = linearLayoutManager
            adapter = myCartAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            myCartAdapter.submitList(state.cartUiItems)

            state.errorMessage?.let { error ->
                when {
                    error.contains("Failed adding to cart") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_add_to_cart),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed removing from cart") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_remove_from_cart),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed getting cart items") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_cart_items),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed creating new order") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_create_new_order),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed getting current user id") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_user_id),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    else -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_cart_general),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                }
            }

            if (state.isCreationSuccessful) {
                Utils.showSuccessSnackbar(
                    this.requireView(), getString(R.string.successful_order_creation),
                    Snackbar.LENGTH_LONG
                )
                viewModel.clearSuccess()
            }

            binding.tvEmptyCart.visibility =
                if (state.cartUiItems.isEmpty() && state.isInitialized) View.VISIBLE else View.GONE
            binding.textView.visibility = if (state.isInitialized) View.GONE else View.VISIBLE
            binding.imageBtnMakeOrder.isEnabled =
                state.cartUiItems.isNotEmpty() && state.paymentMethod != CartViewModel.PaymentMethod.NOT_CHOSEN

            binding.tvTotalCostValue.text =
                "${String.format("%.2f", calculateTotalCost(state.cartUiItems))} ₽"
        }
    }

    private fun setupSpinner() {
        val paymentAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.payment_methods,
            android.R.layout.simple_spinner_item
        )
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner2.adapter = paymentAdapter

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selectedMethod = when (p2) {
                    0 -> CartViewModel.PaymentMethod.NOT_CHOSEN
                    1 -> CartViewModel.PaymentMethod.CARD_ONLINE
                    2 -> CartViewModel.PaymentMethod.CARD_UPON_RECEIPT
                    3 -> CartViewModel.PaymentMethod.CASH
                    4 -> CartViewModel.PaymentMethod.FPS
                    5 -> CartViewModel.PaymentMethod.CRYPTO
                    else -> CartViewModel.PaymentMethod.NOT_CHOSEN
                }
                viewModel.onPaymentMethodSelected(selectedMethod)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // ничего
            }

        }
    }

    private fun calculateTotalCost(list: List<CatalogueUiItem>): Float {
        var totalCost = 0f
        list.forEach { it -> totalCost += it.quantityInCart * it.product.price }
        return totalCost
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // предотвращаем утечки памяти
        _binding = null
    }
}