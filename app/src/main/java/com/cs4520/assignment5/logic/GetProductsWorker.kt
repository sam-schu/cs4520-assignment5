package com.cs4520.assignment5.logic

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.cs4520.assignment5.ApiService
import com.cs4520.assignment5.RetrofitBuilder
import com.cs4520.assignment5.model.Repo

@SuppressLint("StaticFieldLeak")
object WorkManagerProvider {
    private var workManager: WorkManager? = null

    private var workManagerContext: Context? = null

    fun getWorkManager(): WorkManager? {
        if (workManager == null) {
            workManagerContext?.let {
                workManager = WorkManager.getInstance(it)
            }
        }
        return workManager
    }

    fun setContext(context: Context) {
        workManagerContext = context
    }
}

class GetProductsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val apiService = RetrofitBuilder.getRetrofit().create(ApiService::class.java)
        val repo = Repo()

        // adds new products from the API into the database if possible
        val result = ProductLoader.loadProductData(apiService, repo)

        return if (result is DisplayProducts.LoadUnsuccessful) {
            Result.failure()
        } else {
            Result.success()
        }
    }
}