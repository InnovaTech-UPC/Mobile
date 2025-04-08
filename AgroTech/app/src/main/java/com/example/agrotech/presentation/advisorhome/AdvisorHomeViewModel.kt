package com.example.agrotech.presentation.advisorhome

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.common.Resource
import com.example.agrotech.data.repository.advisor.AdvisorRepository
import com.example.agrotech.data.repository.appointment.AppointmentRepository
import com.example.agrotech.data.repository.profile.ProfileRepository
import com.example.agrotech.data.repository.farmer.FarmerRepository // Importa el repositorio de farmers
import com.example.agrotech.domain.appointment.Appointment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class AdvisorHomeViewModel(
    private val advisorRepository: AdvisorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val profileRepository: ProfileRepository,
    private val farmerRepository: FarmerRepository
) : ViewModel() {

    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    var advisorId by mutableStateOf<Long?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var advisorName by mutableStateOf<String?>(null)
        private set

    var farmerNames by mutableStateOf<Map<Long, String>>(emptyMap())
        private set

    fun loadData() {
        viewModelScope.launch {
            if (GlobalVariables.TOKEN.isBlank() || GlobalVariables.USER_ID == 0L) {
                return@launch
            }
            fetchAdvisorAndAppointments()
        }
    }

    private fun fetchAdvisorAndAppointments() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Obtener información del asesor
                val advisorResult = advisorRepository.searchAdvisorByUserId(GlobalVariables.USER_ID, GlobalVariables.TOKEN)
                if (advisorResult is Resource.Success) {
                    advisorId = advisorResult.data?.id
                    fetchAdvisorName(GlobalVariables.USER_ID)
                    advisorId?.let {
                        fetchAppointments(it) // Obtener las citas usando el advisorId
                        fetchFarmerNames() // Obtener los nombres de los agricultores
                    }
                } else if (advisorResult is Resource.Error) {
                    errorMessage = advisorResult.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun fetchAppointments(advisorId: Long) {
        val appointmentsResult = appointmentRepository.getAppointmentsByAdvisor(advisorId, GlobalVariables.TOKEN)

        if (appointmentsResult is Resource.Success) {
            appointments = appointmentsResult.data ?: emptyList()
        } else if (appointmentsResult is Resource.Error) {
            errorMessage = appointmentsResult.message
        }
    }

    private suspend fun fetchFarmerNames() {
        val farmersNamesMap = mutableMapOf<Long, String>()

        val farmerNameResults = appointments.map { appointment ->
            viewModelScope.async {
                try {
                    val farmerProfileResult = farmerRepository.searchFarmerByFarmerId(appointment.farmerId, GlobalVariables.TOKEN)

                    if (farmerProfileResult is Resource.Success) {
                        val profileResult = profileRepository.searchProfile(farmerProfileResult.data?.userId ?: 0L, GlobalVariables.TOKEN)

                        val farmerName = if (profileResult is Resource.Success) {
                            "${profileResult.data?.firstName} ${profileResult.data?.lastName}"
                        } else {
                            "Name not found"
                        }

                        farmersNamesMap[appointment.id] = farmerName
                    } else {
                        farmersNamesMap[appointment.id] = "Name not found"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error fetching farmer profile: ${e.localizedMessage}"
                    farmersNamesMap[appointment.id] = "Name not found"
                }
            }
        }
        farmerNameResults.awaitAll()

        farmerNames = farmersNamesMap
    }


    private suspend fun fetchAdvisorName(userId: Long) {
        val profileResult = profileRepository.searchProfile(userId, GlobalVariables.TOKEN)
        if (profileResult is Resource.Success) {
            advisorName = profileResult.data?.firstName + " " + profileResult.data?.lastName
        } else {
            errorMessage = profileResult.message
        }
    }
}
