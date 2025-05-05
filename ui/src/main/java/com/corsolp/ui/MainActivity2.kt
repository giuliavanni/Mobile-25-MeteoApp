package com.corsolp.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity2 : AppCompatActivity() {

    companion object   {
        private val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // ✅ Permesso già concesso
            enableLocationFeatures()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // ℹ️ Mostriamo il Rationale perché l'utente ha negato il permesso una volta
            showRationaleDialog()
        } else {
            // 🔄 Richiediamo direttamente il permesso
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permesso necessario")
            .setMessage("Questa app ha bisogno della tua posizione per funzionare correttamente.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun enableLocationFeatures() {
        Toast.makeText(this, "Permesso Accettato, well done!", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Permesso concesso
                enableLocationFeatures()
            } else {
                // ❌ Permesso negato, possiamo aprire le impostazioni se necessario
                Toast.makeText(this, "Permesso negato, abilitalo nelle impostazioni", Toast.LENGTH_LONG).show()
            }
        }
    }
}
