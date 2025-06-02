package com.corsolp.ui.utils

import android.content.Context


object TemperatureUtils {
    /**
     * Converte la temperatura da Celsius all'unità preferita salvata nelle SharedPreferences.
     * Restituisce una coppia: temperatura convertita e simbolo unità.
     */
    fun convertTemperature(context: Context, tempCelsius: Double): Pair<Double, String> {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val tempUnit = prefs.getString("temp_unit", "Celsius") ?: "Celsius"

        return if (tempUnit == "Fahrenheit") {
            val tempF = tempCelsius * 9 / 5 + 32
            Pair(tempF, "°F")
        } else {
            Pair(tempCelsius, "°C")
        }
    }
}