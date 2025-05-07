package com.example.agrotech.presentation.farmerhistory

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.common.Resource
import com.example.agrotech.common.Routes
import com.example.agrotech.common.UIState
import com.example.agrotech.data.repository.advisor.AdvisorRepository
import com.example.agrotech.data.repository.appointment.AppointmentRepository
import com.example.agrotech.data.repository.appointment.AvailableDateRepository
import com.example.agrotech.data.repository.farmer.FarmerRepository
import com.example.agrotech.data.repository.profile.ProfileRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmerHistoryViewModel(
    private val navController: NavController,
    private val availableDateRepository: AvailableDateRepository,
    private val profileRepository: ProfileRepository,
    private val advisorRepository: AdvisorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val farmerRepository: FarmerRepository
) : ViewModel() {

    private val _state = mutableStateOf(UIState<List<AppointmentCard>>())
    val state: State<UIState<List<AppointmentCard>>> get() = _state

    fun goBack() {
        navController.popBackStack()
    }

    fun onReviewAppointment(appointmentId: Long) {
        navController.navigate(Routes.FarmerReviewAppointment.route + "/$appointmentId")
    }

    fun getFarmerHistory(selectedDate: Date? = null) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val farmerResult = farmerRepository.searchFarmerByUserId(GlobalVariables.USER_ID, GlobalVariables.TOKEN)

            if (farmerResult !is Resource.Success || farmerResult.data == null) {
                _state.value = UIState(message = "Error al intentar obtener información del usuario")
                return@launch
            }

            val farmerId = farmerResult.data.id
            val appointmentResult = appointmentRepository.getAppointmentsByFarmer(farmerId, GlobalVariables.TOKEN)

            if (appointmentResult !is Resource.Success || appointmentResult.data == null) {
                _state.value = UIState(message = "Error al intentar obtener las citas")
                return@launch
            }

            // Filtrar citas completadas o revisadas
            val filteredAppointments = appointmentResult.data.filter {
                it.status == "COMPLETED" || it.status == "REVIEWED"
            }

            val appointmentCards = mutableListOf<AppointmentCard>()
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            for (appointment in filteredAppointments) {
                // Obtener fecha disponible asociada
                val availableDateResult = availableDateRepository.getAvailableDateById(appointment.availableDateId, GlobalVariables.TOKEN)
                val availableDate = (availableDateResult as? Resource.Success)?.data

                if (availableDate == null) continue

                // Filtrar por fecha seleccionada (si aplica)
                if (selectedDate != null) {
                    val formattedSelectedDate = dateFormatter.format(selectedDate)
                    if (!availableDate.scheduledDate.startsWith(formattedSelectedDate)) continue
                }

                // Obtener nombre y foto del asesor
                val advisorResult = advisorRepository.searchAdvisorByAdvisorId(availableDate.advisorId, GlobalVariables.TOKEN)
                val advisor = (advisorResult as? Resource.Success)?.data

                val profile = advisor?.userId?.let { userId ->
                    val profileResult = profileRepository.searchProfile(userId, GlobalVariables.TOKEN)
                    (profileResult as? Resource.Success)?.data
                }

                val advisorName = "${profile?.firstName ?: "Asesor"} ${profile?.lastName ?: "Desconocido"}"
                val advisorPhoto = profile?.photo ?: "Asesor Desconocido"

                appointmentCards.add(
                    AppointmentCard(
                        id = appointment.id,
                        advisorName = advisorName,
                        advisorPhoto = advisorPhoto,
                        message = appointment.message,
                        status = appointment.status,
                        scheduledDate = availableDate.scheduledDate,
                        startTime = availableDate.startTime,
                        endTime = availableDate.endTime,
                        meetingUrl = appointment.meetingUrl
                    )
                )
            }

            // Ordenar por fecha y hora
            val sortedCards = appointmentCards.sortedWith(compareBy(
                { dateFormatter.parse(it.scheduledDate) },
                { timeFormatter.parse(it.startTime) }
            ))

            _state.value = if (sortedCards.isNotEmpty()) {
                UIState(data = sortedCards)
            } else {
                UIState(message = "No se encontraron citas previas")
            }
        }
    }


}
