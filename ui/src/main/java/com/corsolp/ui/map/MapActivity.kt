package com.corsolp.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R



class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Mostra MapFragment solo al primo avvio
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_fragment_container, MapsFragment())
                .commit()
        }
    }

    // Gestisce il tasto "indietro" sulla ActionBar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}



