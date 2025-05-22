package com.corsolp.domain.repository

import com.corsolp.domain.model.AccomodationType
import kotlinx.coroutines.flow.StateFlow

interface AccomodationRepository {

    val accomodationTypeList: StateFlow<List<AccomodationType>>

    fun start()

    fun fetchAccomodationTypeList()
}