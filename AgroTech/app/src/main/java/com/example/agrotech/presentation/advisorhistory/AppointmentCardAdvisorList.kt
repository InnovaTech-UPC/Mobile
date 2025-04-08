package com.example.agrotech.presentation.advisorhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.agrotech.domain.appointment.Appointment
import com.example.agrotech.presentation.advisorhome.AdvisorHomeViewModel

@Composable
fun AppointmentCardAdvisorList(
    appointments: List<Appointment>,
    farmerNames: Map<Long, String>,
    viewModel: AdvisorHomeViewModel
) {
    Column {
        appointments.forEach { appointment ->
            val farmerName = farmerNames[appointment.id] ?: "Name not found"
            AppointmentCardAdvisor(
                appointment = appointment,
                farmerName = farmerName,
                viewModel = viewModel
            )
        }
    }
}