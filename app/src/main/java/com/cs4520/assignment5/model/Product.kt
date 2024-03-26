package com.cs4520.assignment5.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a row in the Product table, with a unique ID, product name, product type (should be
 * either "Equipment" or "Food", and product price, and possibly an expiry date. Is also used to
 * represent a product just obtained from the server.
 */
@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val type: String?,
    val expiryDate: String?,
    val price: Double?
)
