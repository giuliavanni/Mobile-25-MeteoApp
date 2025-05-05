package com.corsolp.lessonactivitiesfragments

import android.app.Application
import com.corsolp.data.di.RepositoryProviderImpl
import com.corsolp.domain.di.UseCaseProvider

class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        UseCaseProvider.setup(
            repositoryProvider = RepositoryProviderImpl()
        )
    }
}