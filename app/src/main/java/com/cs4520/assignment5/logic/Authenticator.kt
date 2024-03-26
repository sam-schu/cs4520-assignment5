package com.cs4520.assignment5.logic

/**
 * Manages authentication for the app's login page.
 */
class Authenticator {
    private data class AuthenticationPair(private val username: String,
                                          private val password: String)

    /**
     * Returns whether the given username and password are valid for entry into the product list
     * fragment.
     */
    fun authenticate(username: String, password: String): Boolean =
        ALLOWED_CREDENTIALS.contains(AuthenticationPair(username, password))

    private companion object {
        val ALLOWED_CREDENTIALS = listOf(AuthenticationPair("admin", "admin"))
    }
}