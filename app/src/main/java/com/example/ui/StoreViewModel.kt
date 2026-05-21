package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FavProduct
import com.example.model.Category
import com.example.model.Product
import com.example.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface StoreUiState {
    object Loading : StoreUiState
    data class Success(val products: List<Product>) : StoreUiState
    data class Error(val message: String) : StoreUiState
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StoreRepository()
    private val database = AppDatabase.getDatabase(application)
    private val favoritesDao = database.favoritesDao

    // State controllers
    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Room database favorites with in-memory fallback
    private val _favoritesList = MutableStateFlow<List<FavProduct>>(emptyList())
    val favoritesList: StateFlow<List<FavProduct>> = _favoritesList.asStateFlow()

    init {
        // Collect database favorites within a try-catch launch block
        viewModelScope.launch {
            try {
                favoritesDao.getAllFavorites()
                    .catch { e ->
                        Log.e("StoreViewModel", "Error in database favorites Flow collection, falling back", e)
                        emit(emptyList())
                    }
                    .collect { list ->
                        _favoritesList.value = list
                    }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Fatal error initializing favorites monitoring. Keeping custom local favorites lists.", e)
            }
        }
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading
            try {
                // Fetch categories
                val fetchedCategories = repository.fetchCategories()
                _categories.value = fetchedCategories
                
                // Keep selected category if it's still in the newly fetched list, else pick the first
                val currentCategory = _selectedCategory.value
                val matched = fetchedCategories.find { it.name == currentCategory?.name }
                _selectedCategory.value = matched ?: fetchedCategories.firstOrNull()

                loadProducts()
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error loading shop data", e)
                _uiState.value = StoreUiState.Error("Não foi possível carregar a Ticia Jeans. Verifique sua conexão.")
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val fetchedCategories = repository.fetchCategories()
                _categories.value = fetchedCategories
                
                // Load products based on current selection
                val prods = repository.fetchProducts(
                    page = 1,
                    categoryUrl = _selectedCategory.value?.url
                )
                _uiState.value = StoreUiState.Success(prods)
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error refreshing data", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectCategory(category: Category) {
        if (_selectedCategory.value?.name == category.name) return
        _selectedCategory.value = category
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading
            try {
                val valToFetch = if (category.name == "Ver Tudo") null else category.url
                val prods = repository.fetchProducts(page = 1, categoryUrl = valToFetch)
                _uiState.value = StoreUiState.Success(prods)
            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error("Erro ao carregar categoria ${category.name}")
            }
        }
    }

    private suspend fun loadProducts() {
        val catUrl = _selectedCategory.value?.let { if (it.name == "Ver Tudo") null else it.url }
        val prods = repository.fetchProducts(page = 1, categoryUrl = catUrl)
        _uiState.value = StoreUiState.Success(prods)
    }

    fun searchProduct(query: String) {
        _searchQuery.value = query
    }

    // Room Favorites persistence operations
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            val fav = FavProduct(
                id = product.id,
                title = product.title,
                url = product.url,
                imageUrl = product.imageUrl,
                price = product.price,
                isSale = product.isSale,
                category = product.category,
                description = product.description
            )
            try {
                if (favoritesDao.isFavorite(product.id)) {
                    favoritesDao.deleteFavorite(fav)
                    Log.i("StoreViewModel", "Removed favorite via DB: ${product.title}")
                } else {
                    favoritesDao.insertFavorite(fav)
                    Log.i("StoreViewModel", "Added favorite via DB: ${product.title}")
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error toggling favorite in DB for product: ${product.title}, falling back to in-memory state", e)
                val current = _favoritesList.value
                val isFav = current.any { it.id == product.id }
                if (isFav) {
                    _favoritesList.value = current.filter { it.id != product.id }
                } else {
                    _favoritesList.value = current + fav
                }
            }
        }
    }

    fun removeFavorite(favProduct: FavProduct) {
        viewModelScope.launch {
            try {
                favoritesDao.deleteFavorite(favProduct)
                Log.i("StoreViewModel", "Removed favorite via DB: ${favProduct.title}")
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error removing favorite from DB: ${favProduct.title}, falling back to in-memory state", e)
                _favoritesList.value = _favoritesList.value.filter { it.id != favProduct.id }
            }
        }
    }

    fun isProductFavorite(productId: String): Flow<Boolean> {
        return try {
            favoritesDao.isFavoriteFlow(productId)
        } catch (e: Exception) {
            flowOf(false)
        }
    }
}
