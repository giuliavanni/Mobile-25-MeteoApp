package com.corsolp.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Prendi i dati passati dall'Intent
        val cityName = intent.getStringExtra("city_name") ?: "Località sconosciuta"
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Mostra MapsFragment con i dati
        if (savedInstanceState == null) {
            val mapFragment = MapsFragment.newInstance(latitude, longitude, cityName)
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_fragment_container, mapFragment)
                .commit()
        }
    }

    // Gestisce il tasto "indietro" sulla ActionBar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}


