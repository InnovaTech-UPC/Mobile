package com.example.agrotech.presentation.farmerappointments

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
import com.example.agrotech.presentation.farmerhistory.AppointmentCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmerAppointmentListViewModel(
    private val navController: NavController,
    private val availableDateRepository: AvailableDateRepository,
    private val appointmentRepository: AppointmentRepository,
    private val farmerRepository: FarmerRepository
) : ViewModel() {

    private val _state = mutableStateOf(UIState<List<AppointmentCard>>())
    val state: State<UIState<List<AppointmentCard>>> get() = _state


    fun goBack() {
        navController.navigate(Routes.FarmerHome.route)
    }

    fun goHistory() {
        navController.navigate(Routes.FarmerAppointmentHistory.route)
    }

    fun goAppointmentDetail(appointmentId: Long) {
        navController.navigate(Routes.FarmerAppointmentDetail.route + "/$appointmentId")
    }

    fun getAdvisorAppointmentListByFarmer(selectedDate: Date? = null) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val farmerResult = farmerRepository.searchFarmerByUserId(GlobalVariables.USER_ID, GlobalVariables.TOKEN)

            if (farmerResult is Resource.Success && farmerResult.data != null) {
                val farmerId = farmerResult.data.id
                val result = appointmentRepository.getAllAppointments(GlobalVariables.TOKEN)

                if (result is Resource.Success && result.data != null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formattedSelectedDate = selectedDate?.let { dateFormat.format(it) }

                    val appointmentCards = mutableListOf<AppointmentCard>()

                    val filteredAppointments = result.data
                        .filter { it.farmerId == farmerId && (it.status == "PENDING" || it.status == "ONGOING") }

                    val appointmentsWithDates = filteredAppointments.mapNotNull { appointment ->
                        val availableDateResult = availableDateRepository.getAvailableDateById(
                            appointment.availableDateId,
                            GlobalVariables.TOKEN
                        )

                        if (availableDateResult is Resource.Success && availableDateResult.data != null) {
                            val availableDate = availableDateResult.data

                            // Si hay una fecha seleccionada, filtramos
                            if (formattedSelectedDate != null &&
                                availableDate.scheduledDate != formattedSelectedDate
                            ) {
                                return@mapNotNull null
                            }

                            Pair(appointment, availableDate)
                        } else {
                            null
                        }
                    }

                    val sortedAppointments = appointmentsWithDates.sortedWith(
                        compareBy(
                            { dateFormat.parse(it.second.scheduledDate) },
                            { timeFormat.parse(it.second.startTime) }
                        )
                    )

                    for ((appointment, availableDate) in sortedAppointments) {
                        appointmentCards.add(
                            AppointmentCard(
                                id = appointment.id,
                                advisorName = "Asesor Desconocido", // Puedes rellenarlo luego si quieres
                                advisorPhoto = "Asesor Desconocido",
                                message = appointment.message,
                                status = appointment.status,
                                scheduledDate = availableDate.scheduledDate,
                                startTime = availableDate.startTime,
                                endTime = availableDate.endTime,
                                meetingUrl = appointment.meetingUrl
                            )
                        )
                    }

                    if (appointmentCards.isNotEmpty()) {
                        _state.value = UIState(data = appointmentCards)
                    } else {
                        _state.value = UIState(message = "No se encontraron citas para la fecha seleccionada")
                    }

                } else {
                    _state.value = UIState(message = "Error al intentar obtener las citas")
                }
            } else {
                _state.value = UIState(message = "Error al intentar obtener información del usuario")
            }
        }
    }


}
