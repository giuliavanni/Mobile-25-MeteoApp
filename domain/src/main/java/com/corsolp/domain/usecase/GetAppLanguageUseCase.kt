package com.corsolp.domain.usecase

import com.corsolp.domain.repository.SettingsRepository

class GetAppLanguageUseCase(private val repository: SettingsRepository) {
    fun execute(): String {
        return repository.getLanguage()
    }
}