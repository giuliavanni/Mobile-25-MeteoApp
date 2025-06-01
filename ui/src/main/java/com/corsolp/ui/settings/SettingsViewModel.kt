package com.corsolp.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.corsolp.domain.repository.SettingsRepository

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val selectedLanguage = MutableLiveData<String>()
    val selectedTheme = MutableLiveData<String>()
    val selectedTempUnit = MutableLiveData<String>()

    fun loadSettings() {
        selectedLanguage.value = repository.getLanguage()
        selectedTheme.value = repository.getTheme()
        selectedTempUnit.value = repository.getTempUnit()
    }

    fun saveSettings(lang: String, theme: String, tempUnit: String) {
        repository.setLanguage(lang)
        repository.setTheme(theme)
        repository.setTempUnit(tempUnit)
        selectedLanguage.value = lang
        selectedTheme.value = theme
        selectedTempUnit.value = tempUnit
    }

    fun saveLanguage(lang: String) {
        repository.setLanguage(lang)
        selectedLanguage.value = lang
    }

    fun saveTheme(theme: String) {
        repository.setTheme(theme)
        selectedTheme.value = theme
    }

    fun saveTempUnit(tempUnit: String) {
        repository.setTempUnit(tempUnit)
        selectedTempUnit.value = tempUnit
    }
}
