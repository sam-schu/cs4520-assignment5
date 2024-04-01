package com.cs4520.assignment5.model

/**
 * Defines methods that can be used to access/update the locally stored products.
 */
interface ProductRepo {
    /**
     * Replaces all stored products with the given products.
     */
    fun replaceStoredProducts(products: List<Product>)

    /**
     * Gets all stored products. Returns null if the database object cannot be obtained.
     */
    fun getStoredProducts(): List<Product>?

    /**
     * Adds products that are not already in the database.
     */
    fun addNewProducts(products: List<Product>)
}