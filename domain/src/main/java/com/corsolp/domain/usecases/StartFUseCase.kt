package com.corsolp.domain.usecases

import com.corsolp.domain.repository.AccomodationRepository

interface StartFUseCase: () -> Unit

class StartFUseCaseImpl(
    private val accomodationRepository: AccomodationRepository
): StartFUseCase {
    override fun invoke() {
        accomodationRepository.start()
    }
}