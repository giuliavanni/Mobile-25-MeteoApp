package com.corsolp.domain.repository

interface SettingsRepository {
    fun getLanguage(): String
    fun setLanguage(language: String)
    fun getTheme(): String
    fun setTheme(theme: String)
    fun getTempUnit(): String
    fun setTempUnit(unit: String)
}
