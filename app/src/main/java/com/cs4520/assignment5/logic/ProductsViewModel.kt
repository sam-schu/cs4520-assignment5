package com.cs4520.assignment5.logic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import java.util.concurrent.TimeUnit

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
     * Represents that a list of products was obtained either from the server, or from the local
     * database if the device was offline. Holds this list of products.
     */
    data class ProductList(val products: List<CategorizedProduct>) : DisplayProducts

    data class LoadUnsuccessful(val reason: Reason) : DisplayProducts {
        sealed interface Reason {
            /**
             * Represents that no products could be obtained because the server gave an error.
             */
            data object ServerError : Reason

            /**
             * Represents that no products could be obtained because the server gave an empty response.
             */
            data object ServerNoProducts : Reason

            /**
             * Represents that no products could be obtained because the device was offline, and either no
             * products had been stored in the local database or the database access failed.
             */
            data object OfflineNoProducts : Reason
        }
    }
}

object ProductLoader {
    suspend fun loadProductData(apiService: ApiService, repo: ProductRepo): DisplayProducts {
        try {
            val products = apiService.getAllProducts().distinct().filterNot {
                it.type == null || it.name == null || it.price == null
            }
            if (products.isEmpty()) {
                return DisplayProducts.LoadUnsuccessful(
                    DisplayProducts.LoadUnsuccessful.Reason.ServerNoProducts
                )
            } else {
                repo.addNewProducts(products)
                repo.getStoredProducts()?.map { it.toCategorizedProduct() }?.let {
                    return DisplayProducts.ProductList(it)
                }
                return DisplayProducts.LoadUnsuccessful(
                    DisplayProducts.LoadUnsuccessful.Reason.ServerError
                )
            }
        } catch (e: HttpException) {
            return DisplayProducts.LoadUnsuccessful(
                DisplayProducts.LoadUnsuccessful.Reason.ServerError
            )
        } catch (e: UnknownHostException) {
            // handle device offline
            val products = repo.getStoredProducts()
            return if (products.isNullOrEmpty()) {
                DisplayProducts.LoadUnsuccessful(
                    DisplayProducts.LoadUnsuccessful.Reason.OfflineNoProducts
                )
            } else {
                val categorizedProducts = products.map { it.toCategorizedProduct() }
                DisplayProducts.ProductList(categorizedProducts)
            }
        }
    }
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

    private var workRequestLiveData: LiveData<WorkInfo>? = null
    private var workRequestLiveDataObserver: Observer<in WorkInfo>? = null

    init {
        loadProductData()
        scheduleGetProductsWorker()
    }

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
        val apiService = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        viewModelScope.launch(Dispatchers.IO) {
            val productData = ProductLoader.loadProductData(apiService, repo)
            withContext(Dispatchers.Main) {
                _displayProducts.value = productData
            }
        }
    }

    private fun scheduleGetProductsWorker() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<GetProductsWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        val workManager: WorkManager? = WorkManagerProvider.getWorkManager()

        workManager?.apply {
            cancelAllWork()
            enqueue(workRequest)
            workRequestLiveData = getWorkInfoByIdLiveData(workRequest.id)
            val observer = Observer<WorkInfo> { result ->
                if (result.state == WorkInfo.State.SUCCEEDED) {
                    repo.getStoredProducts()?.map { it.toCategorizedProduct() }?.let {
                        _displayProducts.value = DisplayProducts.ProductList(it)
                    }
                }
            }
            workRequestLiveData?.observeForever(observer)
            workRequestLiveDataObserver = observer
        }
    }

    override fun onCleared() {
        workRequestLiveDataObserver?.let {
            workRequestLiveData?.removeObserver(it)
        }
        super.onCleared()
    }
}