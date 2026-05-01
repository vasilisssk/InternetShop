package com.game.internetshop.views.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.game.internetshop.R
import com.game.internetshop.databinding.FragmentCatalogueBinding
import com.game.internetshop.viewmodels.CatalogueViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils
import kotlin.getValue

class CatalogueFragment: Fragment() {
    private val viewModel: CatalogueViewModel by viewModel()
    private var _binding: FragmentCatalogueBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCatalogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupSearchView()
        setupSpinner()
        observeViewModel()

        binding.fabToTop.setOnClickListener {
            binding.rv.scrollToPosition(0)
        }
    }

    private fun setupRecyclerView() {
        val rv: RecyclerView = binding.rv
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)

        // создаем адаптер с колбеками для кнопок +/-
        myAdapter = ProductAdapter(
            onIncreaseButtonClick = {
                productId -> viewModel.onAddingToCart(productId)
            },
            onDecreaseButtonClick = {
                productId -> viewModel.onRemovingFromCart(productId)
            }
        )

        rv.apply {
            layoutManager = gridLayoutManager
            adapter = myAdapter
            setHasFixedSize(true)
        }

        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisiblePosition > 0) {
                    binding.fabToTop.show()
                } else {
                    binding.fabToTop.hide()
                }
            }
        })
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onSearchQueryChanged(p0 ?: "")
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                // не обрабатывать отдельно, так как текст меняется при каждом вводе
                return false
            }
        })

        searchView.setOnCloseListener {
            viewModel.onSearchQueryChanged("")
            // с false SearchView сам очистит текст
            false
        }
    }

    private fun setupSpinner() {
        // создание адаптера для Spinner с фильтрами
        val filterAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_filters,
            android.R.layout.simple_spinner_item
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner.adapter = filterAdapter

        // обработка вызова фильтра
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedFilter = when (p2) {
                    0 -> CatalogueViewModel.ProductFilter.NONE
                    1 -> CatalogueViewModel.ProductFilter.PRICE_LOW_TO_HIGH
                    2 -> CatalogueViewModel.ProductFilter.PRICE_HIGH_TO_LOW
                    3 -> CatalogueViewModel.ProductFilter.NAME_A_TO_Z
                    4 -> CatalogueViewModel.ProductFilter.NAME_Z_TO_A
                    else -> CatalogueViewModel.ProductFilter.NONE
                }
                viewModel.onFilterSelected(selectedFilter)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // ничего
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // обновляем адаптер
            myAdapter.submitList(state.catalogueUiItems)

            // показываем/скрываем индикатор загрузки
            //binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            state.errorMessage?.let { error ->
                when {
                    error.contains("Failed getting all products") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_all_products),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
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
                    error.contains("Failed getting current user id") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_user_id),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    else -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_catalogue_general),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                }
            }

            binding.tvEmptySearch.visibility = if (state.catalogueUiItems.isEmpty() && state.isInitialized) View.VISIBLE else View.GONE
            binding.tvLoadingCatalogue.visibility = if (state.isInitialized) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // предотвращаем утечки памяти
        _binding = null
    }
}