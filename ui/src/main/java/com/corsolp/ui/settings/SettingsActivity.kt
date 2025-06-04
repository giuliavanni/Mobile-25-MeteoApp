package com.corsolp.ui.settings

import android.content.Intent
import android.os.Bundle
import com.corsolp.ui.databinding.ActivitySettingsBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.corsolp.data.repository.SettingsRepositoryImpl
import com.corsolp.ui.BaseActivity
import com.corsolp.ui.R
import com.corsolp.ui.mainactivity.MainActivity


class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crea repository e factory
        val repository = SettingsRepositoryImpl(applicationContext)
        val factory = SettingsViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        // Imposta gli adapter per gli spinner con le risorse XML
        val languageAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.languageSpinner.adapter = languageAdapter

        val themeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.themes_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.themeSpinner.adapter = themeAdapter

        val tempUnitAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.temp_units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.tempUnitSpinner.adapter = tempUnitAdapter

        // Carica le impostazioni salvate e aggiorna UI
        viewModel.loadSettings()

        viewModel.selectedLanguage.observe(this) { lang ->
            binding.languageSpinner.setSelection(getLanguagePosition(lang))
        }

        viewModel.selectedTheme.observe(this) { theme ->
            binding.themeSpinner.setSelection(getThemePosition(theme))
        }

        viewModel.selectedTempUnit.observe(this) { tempUnit ->
            binding.tempUnitSpinner.setSelection(getTempUnitPosition(tempUnit))
        }

        // Salva le impostazioni al click del bottone
        binding.saveButton.setOnClickListener {
            val selectedLang = binding.languageSpinner.selectedItem.toString()
            val selectedTheme = binding.themeSpinner.selectedItem.toString()
            val selectedTempUnit = binding.tempUnitSpinner.selectedItem.toString()

            viewModel.saveSettings(selectedLang, selectedTheme, selectedTempUnit)

            Toast.makeText(this, "Impostazioni salvate", Toast.LENGTH_SHORT).show()
            finish()

            // Riavvia l'app alla schermata principale
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getLanguagePosition(lang: String): Int {
        val languages = resources.getStringArray(R.array.languages_array)
        return languages.indexOf(lang).takeIf { it >= 0 } ?: 0
    }

    private fun getThemePosition(theme: String): Int {
        val themes = resources.getStringArray(R.array.themes_array)
        return themes.indexOf(theme).takeIf { it >= 0 } ?: 0
    }

    private fun getTempUnitPosition(tempUnit: String): Int {
        val tempUnits = resources.getStringArray(R.array.temp_units_array)
        return tempUnits.indexOf(tempUnit).takeIf { it >= 0 } ?: 0
    }
}
