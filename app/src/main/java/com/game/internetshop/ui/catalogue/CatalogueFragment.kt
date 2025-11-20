package com.game.internetshop.ui.catalogue

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
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
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
        setupSpinner()
        observeViewModel()
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
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onSearchQueryChanged(p0 ?: "")
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                // не обрабатывать отдельно, так как текст меняется при каждом вводе
                return false
            }
        })

        binding.searchView.setOnCloseListener {
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
                    0 -> ProductFilter.NONE
                    1 -> ProductFilter.PRICE_LOW_TO_HIGH
                    2 -> ProductFilter.PRICE_HIGH_TO_LOW
                    3 -> ProductFilter.NAME_A_TO_Z
                    4 -> ProductFilter.NAME_Z_TO_A
                    else -> ProductFilter.NONE
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
                showError(error)
            }

            binding.tvEmptySearch.visibility = if (state.catalogueUiItems.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showError(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // предотвращаем утечки памяти
        _binding = null
    }
}