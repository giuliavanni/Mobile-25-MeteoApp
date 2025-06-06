package com.corsolp.data.repository

import android.view.textclassifier.TextClassification
import com.corsolp.data.local.CityDao
import com.corsolp.data.mapper.toEntity
import com.corsolp.domain.model.City
import com.corsolp.domain.repository.CityRepository
import com.corsolp.data.mapper.toDomain
import com.corsolp.domain.model.NominatimResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class CityRepositoryImpl(private val dao: CityDao) : CityRepository {

    private val client = OkHttpClient()

    override suspend fun insertCity(city: City) {
        dao.insertCity(city.toEntity())
    }

    override suspend fun getAllCities(): List<City> {
        return dao.getAllCities().map { it.toDomain() }
    }

    override suspend fun deleteCity(city: City) {
        dao.deleteCity(city.toEntity())
    }

    override suspend fun updateCity(city: City) {
        dao.updateCity(city.toEntity())
    }

    override suspend fun searchCities(query: String): List<NominatimResult> = withContext(Dispatchers.IO) {
        val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=json&addressdetails=1&limit=5"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "YourAppName/1.0")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()?.let { json ->
                    return@withContext Gson().fromJson(json, Array<NominatimResult>::class.java).toList()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return@withContext emptyList()
    }


}
