package com.cs4520.assignment5.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room

/**
 * Provides access to a single instance of the ApiAdventuresDatabase.
 */
class ApiAdventuresDatabaseProvider {
    companion object {
        private var db: ApiAdventuresDatabase? = null

        // as done in class, since we are not using dependency injection

        @SuppressLint("StaticFieldLeak")
        private var databaseContext: Context? = null

        /**
         * Gets the instance of the ApiAdventuresDatabase.
         *
         * If the database object has not been created yet, creates and returns it. Otherwise,
         * returns the previously created database object. Returns null if the context has not been
         * set with the setContext method.
         */
        fun getDatabase(): ApiAdventuresDatabase? {
            if (db == null) {
                databaseContext?.let {
                    db = Room.databaseBuilder(
                        it,
                        ApiAdventuresDatabase::class.java,
                        "ApiAdventuresDatabase"
                    ).build()
                }
            }
            return db
        }

        /**
         * Sets the context with which the database instance will be created. Should be called
         * before calling getDatabase.
         */
        fun setContext(context: Context) {
            databaseContext = context
        }
    }
}