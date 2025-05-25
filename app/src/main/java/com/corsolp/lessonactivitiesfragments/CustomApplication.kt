package com.corsolp.lessonactivitiesfragments

import android.app.Application
import com.corsolp.data.database.AppDatabase
import com.corsolp.data.di.RepositoryProviderImpl
import com.corsolp.domain.di.UseCaseProvider



class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(this)
        val cityDao = database.cityDao()

        UseCaseProvider.setup(
            repositoryProvider = RepositoryProviderImpl(cityDao)
        )
    }
}