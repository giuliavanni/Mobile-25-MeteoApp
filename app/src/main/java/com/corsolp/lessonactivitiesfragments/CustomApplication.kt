package com.corsolp.lessonactivitiesfragments

import android.app.Application
import com.corsolp.data.local.AppDatabase
import com.corsolp.domain.di.UseCaseProvider



class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        UseCaseProvider.setup(
            repositoryProvider = com.corsolp.data.di.RepositoryProviderImpl(context = this.applicationContext)
        )
    }
}