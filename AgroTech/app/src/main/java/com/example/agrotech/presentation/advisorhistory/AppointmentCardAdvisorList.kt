package com.example.agrotech.presentation.advisorhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agrotech.domain.appointment.Appointment

@Composable
fun AppointmentCardAdvisorList(
    appointments: List<Appointment>,
    farmerNames: Map<Long, String>,
    farmerImagesUrl: Map<Long, String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tu próxima cita",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )


            appointments.forEachIndexed { index, appointment ->
                val farmerName = farmerNames[appointment.id] ?: "Nombre no disponible"
                val farmerImageUrl = farmerImagesUrl[appointment.id] ?: ""

                AppointmentCardAdvisor(
                    appointment = appointment,
                    farmerName = farmerName,
                    farmerImageUrl = farmerImageUrl
                )

                if (index < appointments.size - 1) {
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
