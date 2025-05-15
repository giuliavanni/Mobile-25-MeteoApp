package com.corsolp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private lateinit var googleMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var cityName: String = "Località sconosciuta"

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        val cityLatLng = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(cityLatLng).title(cityName))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 12f))

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                Log.e("MapsFragment", "Permesso non concesso per la localizzazione", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            latitude = it.getDouble("latitude")
            longitude = it.getDouble("longitude")
            cityName = it.getString("city_name") ?: "Località sconosciuta"
        }

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val centerButton = view.findViewById<FloatingActionButton>(R.id.button_center_on_me)
        centerButton?.setOnClickListener {
            centerMapOnUserLocation()
        }
    }

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

