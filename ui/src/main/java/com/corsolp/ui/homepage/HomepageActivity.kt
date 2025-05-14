package com.corsolp.ui.homepage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.corsolp.domain.di.UseCaseProvider
import com.corsolp.ui.databinding.ActivityHomepageBinding
import kotlinx.coroutines.launch
import android.content.Intent
import com.corsolp.ui.map.MapActivity


class HomepageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    private val viewModel: HomepageViewModel by lazy {
        HomepageViewModel(
            UseCaseProvider.fetchAccomodationTypeListUseCase,
            UseCaseProvider.startFUseCase
        )
    }

    private val TAG = "HomepageActivity_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AccomodationAdapter(dataSet = mutableListOf())/*.filter { it is AccomodationType.Apartment })*/


        binding.btnOpenMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accomodationTypeList.collect { accomodationTypeList ->
                    (binding.recyclerView.adapter as? AccomodationAdapter)?.updateList(
                        newList = accomodationTypeList
                    )
                }
            }
        }

        viewModel.fetchAccomodationTypeList()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startFetchAccomodationTypeList()
    }
}