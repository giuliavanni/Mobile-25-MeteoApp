package com.corsolp.data.di

import android.content.Context
import com.corsolp.data.settings.SettingsManager
import com.corsolp.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override fun getLanguage(): String {
        return SettingsManager.getLanguage(context)
    }

    override fun setLanguage(language: String) {
        SettingsManager.setLanguage(context, language)
    }

    override fun getTheme(): String {
        return SettingsManager.getTheme(context)
    }

    override fun setTheme(theme: String) {
        SettingsManager.setTheme(context, theme)
    }

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun getTempUnit(): String {
        return prefs.getString("temp_unit", "Celsius") ?: "Celsius"
    }

    override fun setTempUnit(unit: String) {
        prefs.edit().putString("temp_unit", unit).apply()
    }
}
