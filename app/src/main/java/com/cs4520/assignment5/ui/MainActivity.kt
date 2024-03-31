package com.cs4520.assignment5.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cs4520.assignment5.model.ApiAdventuresDatabaseProvider

/**
 * The application's single activity, which can host both the login and product list screens.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiAdventuresDatabaseProvider.setContext(applicationContext)

        setContent {
            AppNavHost(
                modifier = Modifier.fillMaxSize(),
                navController = rememberNavController()
            )
        }
    }
}