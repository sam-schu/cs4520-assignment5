package com.cs4520.assignment5.model

/**
 * Stores product data locally so that it can be accessed if the device is offline.
 */
class Repo : ProductRepo {
    override fun replaceStoredProducts(products: List<Product>) {
        getDao()?.replaceStoredProducts(products)
    }

    override fun getStoredProducts(): List<Product>? {
        return getDao()?.getAllProducts()
    }

    override fun addNewProducts(products: List<Product>) {
        getDao()?.let { dao ->
            val existingProducts = dao.getAllProducts()
            dao.addProducts(products.minus(existingProducts.toSet()))
        }
    }

    // Returns the DAO (data access object) for the Product table, or null if the database object
    // cannot be obtained
    private fun getDao(): ProductDao? =
        ApiAdventuresDatabaseProvider
            .getDatabase()
            ?.getProductDao()
}