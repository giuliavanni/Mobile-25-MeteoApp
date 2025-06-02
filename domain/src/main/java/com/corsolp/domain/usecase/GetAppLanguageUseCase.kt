package com.corsolp.domain.usecase

import com.corsolp.domain.repository.SettingsRepository

class GetAppLanguageUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(): String {
        return settingsRepository.getLanguage()
    }
}