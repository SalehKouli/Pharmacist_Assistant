package com.example.pharmacistassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmacistassistant.ProductData
import com.example.pharmacistassistant.readExcelFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchResults = MutableStateFlow<List<ProductData>>(emptyList())
    val searchResults: StateFlow<List<ProductData>> get() = _searchResults

    private val _allProducts = MutableStateFlow<List<ProductData>>(emptyList())

    init {
        loadProducts() // Load products when ViewModel is initialized
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                // Use application context to access assets
                val products = readExcelFile(getApplication(), "your_excel_file.xlsx")
                _allProducts.value = products
                _searchResults.value = products
            } catch (e: Exception) {
                e.printStackTrace() // Handle errors as needed
            }
        }
    }

    fun searchByBarcodeOrTradeName(query: String) {
        viewModelScope.launch {
            _searchResults.value = _allProducts.value.filter {
                it.barcode.contains(query, ignoreCase = true) ||
                        it.tradeName.contains(query, ignoreCase = true)
            }
        }
    }

    fun resetSearch() {
        _searchResults.value = _allProducts.value
    }
}
