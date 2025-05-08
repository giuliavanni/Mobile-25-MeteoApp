package com.corsolp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.databinding.ActivitySplashBinding
import com.corsolp.ui.homepage.HomepageActivity
import com.corsolp.ui.map.MapsFragment

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val TAG = "MainActivity_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            //Se voglio provare l'activity con la demo di richiesta permessi
            //val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }, 500)
    }
}