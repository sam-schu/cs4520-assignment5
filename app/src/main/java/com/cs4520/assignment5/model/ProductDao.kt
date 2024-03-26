package com.cs4520.assignment5.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * Defines a Data Access Object (DAO) for the Product table of the Room database.
 */
@Dao
interface ProductDao {
    /**
     * Inserts the given products into the table.
     */
    @Insert
    fun addProducts(products: List<Product>)

    /**
     * Deletes all products from the table.
     */
    @Query("DELETE FROM Product")
    fun clearProducts()

    /**
     * Replaces all products currently stored in the table with the given products.
     */
    @Transaction
    fun replaceStoredProducts(products: List<Product>) {
        clearProducts()
        addProducts(products)
    }

    /**
     * Gets all products from the table.
     */
    @Query("SELECT * FROM Product")
    fun getAllProducts(): List<Product>
}