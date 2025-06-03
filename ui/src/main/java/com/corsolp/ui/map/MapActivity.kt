package com.corsolp.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R


import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker



class MapActivity : AppCompatActivity() {

    private lateinit var mapView: org.osmdroid.views.MapView

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var cityName: String = "Località sconosciuta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carica la configurazione osmdroid
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContentView(R.layout.activity_map)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        cityName = intent.getStringExtra("cityName") ?: cityName

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)  // Tile source (map style)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(12.0)
        val startPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(startPoint)

        // Aggiungi marker sulla città
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.title = cityName
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}