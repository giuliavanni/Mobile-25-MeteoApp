package com.corsolp.domain.usecases

import com.corsolp.domain.models.AccomodationType
import com.corsolp.domain.repository.AccomodationRepository
import kotlinx.coroutines.flow.StateFlow

interface FetchAccomodationTypeListUseCase: () -> StateFlow<List<AccomodationType>>

class FetchAccomodationTypeListUseCaseImpl(
    private val accomodationRepository: AccomodationRepository
): FetchAccomodationTypeListUseCase {
    override fun invoke(): StateFlow<List<AccomodationType>> {
        return accomodationRepository.accomodationTypeList
    }
}