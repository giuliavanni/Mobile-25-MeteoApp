package com.corsolp.domain.di

import com.corsolp.domain.repository.AccomodationRepository

interface RepositoryProvider {
    val accomodationRepository: AccomodationRepository
}