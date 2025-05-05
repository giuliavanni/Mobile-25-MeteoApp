package com.corsolp.domain.di

import com.corsolp.domain.usecases.FetchAccomodationTypeListUseCase
import com.corsolp.domain.usecases.FetchAccomodationTypeListUseCaseImpl
import com.corsolp.domain.usecases.StartFUseCase
import com.corsolp.domain.usecases.StartFUseCaseImpl

object UseCaseProvider {

    lateinit var fetchAccomodationTypeListUseCase: FetchAccomodationTypeListUseCase
    lateinit var startFUseCase: StartFUseCase

    fun setup(
        repositoryProvider: RepositoryProvider
    ) {
        fetchAccomodationTypeListUseCase = FetchAccomodationTypeListUseCaseImpl(
            accomodationRepository = repositoryProvider.accomodationRepository
        )

        startFUseCase = StartFUseCaseImpl(
            accomodationRepository = repositoryProvider.accomodationRepository
        )
    }
}