package com.example.agrotech.presentation.advisorappointmentdetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun AdvisorAppointmentDetailScreen(
    appointmentId: Long,
    viewModel: AdvisorAppointmentDetailViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentDetails(appointmentId)
    }

    val appointment = viewModel.appointmentDetail.value
    val isExpanded = viewModel.expanded.value

    if (appointment != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.goBack() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Detalles de la cita",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                IconButton(onClick = { viewModel.setExpanded(true) }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(32.dp)
                    )
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { viewModel.setExpanded(false) },
                    offset = DpOffset(x = (2000).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Perfil",
                                style = TextStyle(fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            )
                        },
                        onClick = {
                            // viewModel.goToProfile()
                            // viewModel.setExpanded(false)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Salir",
                                style = TextStyle(fontSize = 16.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            )
                        },
                        onClick = {
                            viewModel.signOut()
                            viewModel.setExpanded(false)
                        }
                    )
                }
            }
            AdvisorAppointmentDetailCard(
                appointment = appointment,
                farmerName = viewModel.farmerProfile.value?.firstName ?: "Nombre no disponible",
                farmerImageUrl = viewModel.farmerProfile.value?.photo ?: ""
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Link de la videoconferencia",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = appointment.meetingUrl ?: "No disponible",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Comentario del usuario",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = appointment.message ?: "No disponible",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            // Botón para cancelar la cita
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.onCancelAppointmentClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "Cancelar Cita",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}