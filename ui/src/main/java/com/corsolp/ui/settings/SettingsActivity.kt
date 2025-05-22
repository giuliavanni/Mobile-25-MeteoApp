package com.corsolp.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.databinding.ActivitySettingsBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.corsolp.data.settings.SettingsManager
import com.corsolp.ui.R
import com.corsolp.ui.mainactivity.MainActivity
import java.util.Locale


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun attachBaseContext(newBase: Context) {
        val lang = SettingsManager.getLanguage(newBase)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Imposta le lingue nel Spinner
        val languages = arrayOf("Italiano", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        // Carica le impostazioni salvate
        val savedLang = SettingsManager.getLanguage(this)
        binding.spinnerLanguage.setSelection(if (savedLang == "en") 1 else 0)

        val savedTheme = SettingsManager.getTheme(this)
        val themeId = if (savedTheme == "Light") R.id.radioLight else R.id.radioDark
        binding.radioGroupTheme.check(themeId)

        // Salva impostazioni
        binding.btnSaveSettings.setOnClickListener {
            val selectedLang = if (binding.spinnerLanguage.selectedItem.toString() == "English") "en" else "it"
            SettingsManager.setLanguage(this, selectedLang)

            val selectedTheme = if (binding.radioGroupTheme.checkedRadioButtonId == R.id.radioLight) "Light" else "Dark"
            SettingsManager.setTheme(this, selectedTheme)

            applyTheme(selectedTheme)

            // Ricarica l'activity
            recreate()

            Toast.makeText(this, "Impostazioni salvate!", Toast.LENGTH_SHORT).show()

            // Riavvia l'app alla schermata principale per applicare lingua ovunque
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun applyTheme(theme: String) {
        if (theme == "Light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

}





