package com.game.internetshop.views.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.game.internetshop.R
import com.game.internetshop.databinding.FragmentOrdersBinding
import com.game.internetshop.viewmodels.OrdersViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils
import kotlin.getValue

class OrdersFragment: Fragment() {
    private val viewModel: OrdersViewModel by viewModel()
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var myOrdersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        observeViewModel()
    }

    private fun setupRecycleView() {
        val rv = binding.rvOrders
        val linearLayoutManager = LinearLayoutManager(requireContext())

        myOrdersAdapter = OrdersAdapter()

        rv.apply {
            layoutManager = linearLayoutManager
            adapter = myOrdersAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            myOrdersAdapter.submitList(state.ordersUiItems)

            state.errorMessage?.let { error ->
                when {
                    error.contains("Failed getting current user id") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_user_id),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed getting all user orders") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_all_orders),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    else -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_orders_general),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                }
            }

            binding.tvLoading.visibility = if (state.isInitialized) View.GONE else View.VISIBLE
            binding.textView2.visibility = if (state.isInitialized && state.ordersUiItems.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}