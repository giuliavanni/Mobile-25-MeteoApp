package com.corsolp.domain.di

import com.corsolp.domain.usecase.FetchAccomodationTypeListUseCase
import com.corsolp.domain.usecase.FetchAccomodationTypeListUseCaseImpl
import com.corsolp.domain.usecase.StartFUseCase
import com.corsolp.domain.usecase.StartFUseCaseImpl

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