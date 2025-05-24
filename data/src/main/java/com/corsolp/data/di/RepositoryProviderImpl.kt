package com.corsolp.data.di

import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.domain.repository.AccomodationRepository

class RepositoryProviderImpl: RepositoryProvider {
    override val accomodationRepository: AccomodationRepository = AccomodationRepositoryImpl()
}