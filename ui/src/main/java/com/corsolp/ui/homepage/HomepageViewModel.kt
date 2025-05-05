package com.corsolp.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.models.AccomodationType
import com.corsolp.domain.usecases.FetchAccomodationTypeListUseCase
import com.corsolp.domain.usecases.StartFUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(
    private val fetchAccomodationTypeListUseCase: FetchAccomodationTypeListUseCase,
    private val startFUseCase: StartFUseCase
): ViewModel() {

    private val _accomodationTypeList = MutableStateFlow<List<AccomodationType>>(listOf())
    val accomodationTypeList: StateFlow<List<AccomodationType>> = _accomodationTypeList

    fun startFetchAccomodationTypeList() {
        viewModelScope.launch {
            startFUseCase()
        }
    }

    fun fetchAccomodationTypeList() {
        viewModelScope.launch {
            fetchAccomodationTypeListUseCase().collect { accomodationTypeList ->
                _accomodationTypeList.emit(accomodationTypeList)
            }
        }
    }
}