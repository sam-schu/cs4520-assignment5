package com.cs4520.assignment5.model

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Local Room database for the project containing a table to store product data that can be accessed
 * when the device is offline.
 */
@Database(entities = [Product::class], version = 1)
abstract class ApiAdventuresDatabase : RoomDatabase() {
    /**
     * Returns the DAO (data access object) for the product table.
     */
    abstract fun getProductDao(): ProductDao
}