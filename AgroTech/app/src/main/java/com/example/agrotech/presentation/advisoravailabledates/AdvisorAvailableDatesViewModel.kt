package com.example.agrotech.presentation.advisoravailabledates

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.agrotech.data.repository.appointment.AvailableDateRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.common.Resource
import com.example.agrotech.data.repository.advisor.AdvisorRepository
import com.example.agrotech.domain.appointment.AvailableDate
import kotlinx.coroutines.launch


class AdvisorAvailableDatesViewModel(
    private val navController: NavController,
    private val availableDateRepository: AvailableDateRepository,
    private val advisorRepository: AdvisorRepository
) : ViewModel() {

    private val _availableDates = MutableLiveData<List<AvailableDate>?>()
    val availableDates: LiveData<List<AvailableDate>?> get() = _availableDates

    var advisorId: Long? = null
        private set

    suspend fun initScreen() {
        fetchAdvisorIdAndDates()
    }

    private suspend fun fetchAdvisorIdAndDates() {
        try {
            val advisorResult = advisorRepository.searchAdvisorByUserId(GlobalVariables.USER_ID, GlobalVariables.TOKEN)
            if (advisorResult is Resource.Success) {
                advisorId = advisorResult.data?.id
                fetchAvailableDates()
            } else {
                println("Error fetching advisor ID: ${advisorResult.message}")
            }
        } catch (e: Exception) {
            println("Error fetching advisor ID: ${e.message}")
        }
    }

    fun fetchAvailableDates() {
        viewModelScope.launch {
            try {
                val id = advisorId ?: return@launch
                val result = availableDateRepository.getAvailableDatesByAdvisor(id, GlobalVariables.TOKEN)
                if (result is Resource.Success) {
                    _availableDates.value = result.data
                } else {
                    println("Error fetching available dates: ${result.message}")
                }
            } catch (e: Exception) {
                println("Error fetching available dates: ${e.message}")
            }
        }
    }

    fun createAvailableDate(createAvailableDate: AvailableDate) {
        viewModelScope.launch {
            try {
                val response = availableDateRepository.createAvailableDate(GlobalVariables.TOKEN, createAvailableDate)
                if (response is Resource.Success) {
                    fetchAvailableDates()
                } else {
                    println("Error creating available date: ${response.message}")
                }
            } catch (e: Exception) {
                println("Error creating available date: ${e.message}")
            }
        }
    }
}
