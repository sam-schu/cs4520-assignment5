package com.cs4520.assignment5.logic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs4520.assignment5.ApiService
import com.cs4520.assignment5.RetrofitBuilder
import com.cs4520.assignment5.model.Product
import com.cs4520.assignment5.model.ProductRepo
import com.cs4520.assignment5.model.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Represents a single product in a particular category - either equipment or food.
 *
 * Contains a name, a price, and possibly an expiry date.
 */
sealed class CategorizedProduct(
    open val name: String, open val expiryDate: String?, open val price: Double
) {
    /**
     * Represents a product of the "Equipment" type.
     *
     * Contains a name, a price, and possibly an expiry date.
     */
    data class Equipment(
        override val name: String, override val expiryDate: String?, override val price: Double
    ) : CategorizedProduct(name, expiryDate, price)

    /**
     * Represents a product of the "Food" type.
     *
     * Contains a name, a price, and possibly an expiry date.
     */
    data class Food(
        override val name: String, override val expiryDate: String?, override val price: Double
    ) : CategorizedProduct(name, expiryDate, price)
}

// Converts a Product to a CategorizedProduct of the correct type; throws an
// IllegalStateException if the Product's type is not "Equipment" or "Food"
// or if any of its fields other than the expiry date is null
private fun Product.toCategorizedProduct(): CategorizedProduct {
    if (type == null || name == null || price == null) {
        throw IllegalStateException(
            "The product is missing a type, name, or price and thus cannot be converted to a "
                    + "CategorizedProduct"
        )
    }
    return when (type) {
        "Equipment" -> CategorizedProduct.Equipment(name, expiryDate, price)
        "Food" -> CategorizedProduct.Food(name, expiryDate, price)
        else -> throw IllegalStateException(
            "The product type must be either \"Equipment\" or \"Food\" in order for it to be "
                    + "converted to a CategorizedProduct"
        )
    }
}

/**
 * Holds information about the product list that the view should display.
 */
sealed interface DisplayProducts {
    /**
     * Represents that the app has not finished attempting to load the products.
     */
    data object ProductsNotLoaded : DisplayProducts

    /**
     * Represents that no products could be obtained because the server gave an error.
     */
    data object ServerError : DisplayProducts

    /**
     * Represents that no products could be obtained because the server gave an empty response.
     */
    data object ServerNoProducts : DisplayProducts

    /**
     * Represents that no products could be obtained because the device was offline, and either no
     * products had been stored in the local database or the database access failed.
     */
    data object OfflineNoProducts : DisplayProducts

    /**
     * Represents that a list of products was obtained either from the server, or from the local
     * database if the device was offline. Holds this list of products.
     */
    data class ProductList(val products: List<CategorizedProduct>) : DisplayProducts
}

/**
 * Manages the products to be displayed and interfaces with the server API and local Room database
 * to load and store these products.
 */
class ProductsViewModel(private val repo: ProductRepo = Repo()) : ViewModel() {
    private val _displayProducts = mutableStateOf<DisplayProducts>(
        DisplayProducts.ProductsNotLoaded
    )

    /**
     * Holds the list of products to be displayed, or information about why these products have not
     * been or could not be obtained.
     */
    val displayProducts: State<DisplayProducts> = _displayProducts

    /**
     * Loads product data into the displayProducts property.
     *
     * If the device is online, attempts to load products from the server and updates
     * displayProducts accordingly. If the server returned at least one product, the products
     * stored in the local Room database are replaced with the newly obtained products. If the
     * device is offline, attempts to load products from the database and updates displayProducts
     * accordingly.
     */
    fun loadProductData() {
        val api = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = api.getAllProducts().distinct().filterNot {
                    it.type == null || it.name == null || it.price == null
                }
                if (products.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ServerNoProducts
                    }
                } else {
                    val categorizedProducts = products.map { it.toCategorizedProduct() }
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ProductList(categorizedProducts)
                    }
                    repo.replaceStoredProducts(products)
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _displayProducts.value = DisplayProducts.ServerError
                }
            } catch (e: UnknownHostException) {
                // handle device offline
                val products = repo.getStoredProducts()
                if (products.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.OfflineNoProducts
                    }
                } else {
                    val categorizedProducts = products.map { it.toCategorizedProduct() }
                    withContext(Dispatchers.Main) {
                        _displayProducts.value = DisplayProducts.ProductList(categorizedProducts)
                    }
                }
            }
        }
    }
}