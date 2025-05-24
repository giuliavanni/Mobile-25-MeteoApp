package com.corsolp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.corsolp.ui.databinding.ActivitySplashBinding
import com.corsolp.ui.mainactivity.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val TAG = "MainActivity_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        // Avvia l'animazione
        lottieAnimationView.playAnimation()

        // Configura se l'animazione deve iniziare automaticamente o meno
        lottieAnimationView.loop(true)


        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            //Se voglio provare l'activity con la demo di richiesta permessi
            //val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}