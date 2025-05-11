package com.corsolp.ui.map

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.corsolp.ui.R
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapsFragment : Fragment() {

    // Definisci variabili per latitudine, longitudine e nome della città
    private lateinit var googleMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var cityName: String = "Località sconosciuta"

    // Callback per la mappa
    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        // Aggiungi il marker per la città
        val cityLatLng = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(cityLatLng).title(cityName))

        // Centra la mappa sulla città
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 12f))

        // Imposta il comportamento del bottone "centra sulla mia posizione"
        val centerButton = view?.findViewById<FloatingActionButton>(R.id.button_center_on_me)
        centerButton?.setOnClickListener {
            requestLocationPermission()
        }
    }

    // onCreateView dove vengono recuperati i dati dal Bundle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Recupera latitudine, longitudine e nome della città dal Bundle
        arguments?.let {
            latitude = it.getDouble("latitude")
            longitude = it.getDouble("longitude")
            cityName = it.getString("city_name") ?: "Località sconosciuta"
        }

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Recupera il SupportMapFragment e ottieni la mappa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    // Gestione dei permessi
    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            enableLocationFeatures()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
            showRationaleDialog()
        } else {
            requestPermissions(arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    // Mostra il dialogo per la richiesta del permesso
    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permesso necessario")
            .setMessage("Questa app ha bisogno della tua posizione per funzionare correttamente.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    // Risultato della richiesta permessi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationFeatures()
            } else {
                Toast.makeText(requireContext(), "Permesso negato, abilitalo nelle impostazioni", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Abilita le funzionalità di localizzazione
    private fun enableLocationFeatures() {
        if (::googleMap.isInitialized) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
                centerMapOnUserLocation()
            }
        }
    }

    // Centra la mappa sulla posizione dell'utente
    private fun centerMapOnUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17f))
            } else {
                Toast.makeText(requireContext(), "Posizione non disponibile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100

        // Funzione per creare un'istanza del fragment con i dati della città
        fun newInstance(latitude: Double, longitude: Double, cityName: String): MapsFragment {
            val fragment = MapsFragment()
            val args = Bundle()
            args.putDouble("latitude", latitude)
            args.putDouble("longitude", longitude)
            args.putString("city_name", cityName)
            fragment.arguments = args
            return fragment
        }
    }
}
