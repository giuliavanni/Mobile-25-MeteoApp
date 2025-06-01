package com.corsolp.domain.usecase

import com.corsolp.domain.repository.SettingsRepository

class GetTempUnitUseCase(private val repository: SettingsRepository) {
    fun execute(): String = repository.getTempUnit()
}
