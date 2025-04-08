package com.example.agrotech.presentation.advisorhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agrotech.domain.appointment.Appointment
import com.example.agrotech.presentation.advisorhome.AdvisorHomeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentCardAdvisor(
    appointment: Appointment,
    farmerName: String,
    viewModel: AdvisorHomeViewModel
) {
    val advisorName = viewModel.advisorName ?: "Name not found..."

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Message: ${appointment.message}")
            Text(text = "Status: ${appointment.status}")
            Text(text = "Advisor: $advisorName")
            Text(text = "Farmer: $farmerName")
            Text(text = "Scheduled Date: ${appointment.scheduledDate}")
            Text(text = "Start Time: ${formatDate(appointment.startTime)}")
            Text(text = "End Time: ${formatDate(appointment.endTime)}")
            Text(text = "Meeting URL: ${appointment.meetingUrl}")
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateString)
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: ParseException) {
        try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = timeFormat.parse(dateString)
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time ?: Date())
        } catch (e: ParseException) {
            dateString
        }
    }
}
