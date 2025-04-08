package com.example.agrotech.presentation.advisorhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrotech.domain.appointment.Appointment

@Composable
fun AppointmentCardAdvisorList(
    appointments: List<Appointment>,
    farmerNames: Map<Long, String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF7121)) // Fondo naranja
            .padding(16.dp)
    ) {
        // Título con el subtitulo "Tus próximas citas"
        Text(
            text = "Tus próximas citas",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 24.sp,
            color = Color.White
        )

        // Listado de citas con separadores
        appointments.forEachIndexed { index, appointment ->
            val farmerName = farmerNames[appointment.id] ?: "Name not found"
            AppointmentCardAdvisor(
                appointment = appointment,
                farmerName = farmerName
            )

            // Si no es el último ítem, agregar un separador blanco
            if (index < appointments.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.White
                )
            }
        }
    }
}