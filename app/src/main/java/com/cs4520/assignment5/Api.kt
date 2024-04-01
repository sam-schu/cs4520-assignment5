package com.cs4520.assignment5

import com.cs4520.assignment5.model.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Holds URLs for the server and endpoint used to load product data.
 */
object Api {
    const val BASE_URL: String = "https://kgtttq6tg9.execute-api.us-east-2.amazonaws.com/"
    const val ENDPOINT: String = "prod/random/"
}

/**
 * Provides access to a single Retrofit instance.
 */
object RetrofitBuilder {
    private lateinit var retrofit: Retrofit

    /**
     * Gets the Retrofit instance.
     *
     * If the Retrofit instance has not yet been created, initializes and returns it. Otherwise,
     * returns the previously created Retrofit instance.
     */
    fun getRetrofit(): Retrofit {
        if (!this::retrofit.isInitialized) {
            retrofit = Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}

/**
 * Defines the operations that can be performed to interact with the server API.
 */
interface ApiService {
    /**
     * Gets a list of all products from the server.
     */
    @GET(Api.ENDPOINT)
    suspend fun getAllProducts(): List<Product>
}