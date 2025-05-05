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

    companion object   {
        private val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var googleMap: GoogleMap


    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        val campus = LatLng(44.1478628616173, 12.235671186724646)
        googleMap.addMarker(MarkerOptions().position(campus).title("Campus di Cesena"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(campus, 16f))


        val centerButton = view?.findViewById<FloatingActionButton>(R.id.button_center_on_me)
        centerButton?.setOnClickListener {
            requestLocationPermission()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }



    // Gestione dei permessi
    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            // ✅ Permesso già concesso
            enableLocationFeatures()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
            // ℹ️ L’utente ha già negato una volta → mostriamo una spiegazione
            showRationaleDialog()
        } else {
            // 🔄 Richiediamo direttamente il permesso
            requestPermissions(
                arrayOf(permission),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permesso necessario")
            .setMessage("Questa app ha bisogno della tua posizione per funzionare correttamente.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
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
                // ✅ Permesso concesso
                enableLocationFeatures()
            } else {
                // ❌ Permesso negato, possiamo aprire le impostazioni se necessario
                Toast.makeText(requireContext(), "Permesso negato, abilitalo nelle impostazioni", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun enableLocationFeatures() {
        if (::googleMap.isInitialized) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                centerMapOnUserLocation()
            }
        }
    }


    private fun centerMapOnUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // I permessi non sono stati concessi, quindi esco dalla funzione
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

}