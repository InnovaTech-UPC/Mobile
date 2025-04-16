package com.example.agrotech.presentation.advisorappointmentdetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.data.repository.appointment.AppointmentRepository
import com.example.agrotech.domain.appointment.Appointment
import com.example.agrotech.common.Resource
import com.example.agrotech.common.Routes
import com.example.agrotech.data.repository.authentication.AuthenticationRepository
import com.example.agrotech.data.repository.farmer.FarmerRepository
import com.example.agrotech.data.repository.profile.ProfileRepository
import com.example.agrotech.domain.authentication.AuthenticationResponse
import com.example.agrotech.domain.profile.Profile
import kotlinx.coroutines.launch

class AdvisorAppointmentDetailViewModel(
    private val navController: NavController,
    private val appointmentRepository: AppointmentRepository,
    private val profileRepository: ProfileRepository,
    private val farmerRepository: FarmerRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    val appointmentDetail = mutableStateOf<Appointment?>(null)
    val isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    val farmerProfile = mutableStateOf<Profile?>(null)

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> get() = _expanded

    fun loadAppointmentDetails(appointmentId: Long) {
        viewModelScope.launch {
            try {
                // Obtener los detalles de la cita
                val appointmentResult = appointmentRepository.getAppointmentById(appointmentId, GlobalVariables.TOKEN)
                if (appointmentResult is Resource.Success) {
                    appointmentDetail.value = appointmentResult.data

                    // Obtener el perfil del farmer asociado
                    val farmerId = appointmentResult.data?.farmerId
                    if (farmerId != null) {
                        val farmerResult = farmerRepository.searchFarmerByFarmerId(farmerId, GlobalVariables.TOKEN)
                        if (farmerResult is Resource.Success) {
                            val profileResult = profileRepository.searchProfile(farmerResult.data?.userId ?: 0L, GlobalVariables.TOKEN)
                            if (profileResult is Resource.Success) {
                                farmerProfile.value = profileResult.data
                            } else {
                                errorMessage.value = "Error al cargar el perfil del farmer"
                            }
                        } else {
                            errorMessage.value = "Error al cargar los datos del farmer"
                        }
                    }
                } else {
                    errorMessage.value = "Error al cargar los detalles de la cita"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    private fun goToWelcomeSection() {
        navController.navigate(Routes.Welcome.route)
    }
    fun setExpanded(value: Boolean) {
        _expanded.value = value
    }
    fun goToAppointments() {
        navController.navigate(Routes.AppointmentsAdvisorList.route)
    }

    fun goBack() {
        navController.popBackStack()
    }
    private fun goToCancelAppointmentSuccess() {
        navController.navigate(Routes.ConfirmDeletionAppointmentAdvisor.route)
    }
    suspend fun onCancelAppointmentClick() {
        appointmentRepository.deleteAppointment(appointmentDetail.value?.id ?: 0L, GlobalVariables.TOKEN)
        goToCancelAppointmentSuccess()
    }


    fun signOut() {
        GlobalVariables.ROLES = emptyList()
        viewModelScope.launch {
            val authResponse = AuthenticationResponse(
                id = GlobalVariables.USER_ID,
                username = "",
                token = GlobalVariables.TOKEN
            )
            authenticationRepository.deleteUser(authResponse)
            goToWelcomeSection()
        }
    }


}