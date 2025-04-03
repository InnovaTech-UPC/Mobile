package com.example.agrosupport.presentation.farmerhistory

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agrosupport.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AppointmentCard(appointment: AppointmentCard, onClick: ((Long) -> Unit)? = null) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick(appointment.id) } else Modifier // Hacer clickeable solo si onClick no es nulo
            )
            ,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                modifier = Modifier.size(64.dp).clip(CircleShape).border(4.dp, Color.Gray, CircleShape),
                imageModel = {
                    appointment.advisorPhoto.ifBlank { R.drawable.placeholder }
                },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre la imagen y el texto
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Mantiene el contenido alineado a la izquierda
            ) {
                Text(
                    text = appointment.advisorName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF2B2B2B)
                    ),
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                // Línea divisoria
                Divider(
                    color = Color.Black, // Línea negra
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)

                )
                Text(
                    text = "${appointment.scheduledDate} (${appointment.startTime} - ${appointment.endTime})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06204A)
                    ),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)

                )
            }

        }
    }
}

data class AppointmentCard(
    val id: Long,
    val advisorName: String,
    val advisorPhoto: String,
    val message: String,
    val status: String,
    val scheduledDate: String,
    val startTime: String,
    val endTime: String,
    val meetingUrl: String
)