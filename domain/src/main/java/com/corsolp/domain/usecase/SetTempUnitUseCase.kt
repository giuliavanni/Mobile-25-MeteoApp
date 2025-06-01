package com.corsolp.domain.usecase

import com.corsolp.domain.repository.SettingsRepository

class SetTempUnitUseCase(private val repository: SettingsRepository) {
    fun execute(unit: String) {
        repository.setTempUnit(unit)
    }
}