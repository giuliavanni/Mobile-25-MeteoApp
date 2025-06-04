package com.corsolp.data.repository

import android.content.Context
import com.corsolp.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun getLanguage(): String {
        return prefs.getString("language", "it") ?: "it"
    }

    override fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
    }

    override fun getTheme(): String {
        return prefs.getString("theme", "Light") ?: "Light"
    }

    override fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
    }

    override fun getTempUnit(): String {
        return prefs.getString("temp_unit", "Celsius") ?: "Celsius"
    }

    override fun setTempUnit(unit: String) {
        prefs.edit().putString("temp_unit", unit).apply()
    }
}
