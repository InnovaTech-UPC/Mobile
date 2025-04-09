package com.example.agrotech.presentation.advisorhome

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrotech.presentation.advisorhistory.AppointmentCardAdvisorList

@Composable
fun AdvisorHomeScreen(viewModel: AdvisorHomeViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val appointments = viewModel.appointments
    val advisorId = viewModel.advisorId
    val advisorName = viewModel.advisorName
    val farmerNames = viewModel.farmerNames
    val farmerImagesUrl = viewModel.farmerImagesUrl
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isExpanded = viewModel.expanded.value

    val upcomingAppointments = appointments
        .sortedBy { it.scheduledDate } // Ordenar por fecha
        .take(2) // Tomar solo las 2 primeras

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
            Text(
                text = "Bienvenido(a), ${advisorName ?: "Loading..."}",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.goToNotificationList() }) {
                BadgedBox(
                    badge = {
                        if (viewModel.notificationCount.value > 0) {
                            Badge(
                                contentColor = Color.White,
                                containerColor = Color.Red,
                                modifier = Modifier.offset(x = (-8).dp, y = (0).dp)
                            ) {
                                Text(
                                    text = viewModel.notificationCount.value.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(32.dp)
                    )
                }
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
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        //viewModel.goToProfile()
                        //viewModel.setExpanded(false)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Salir",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        viewModel.signOut()
                        viewModel.setExpanded(false)
                    }
                )
            }
        }

        when {
            isLoading -> {
                LoadingView()
            }
            errorMessage != null -> {
                ErrorView(message = errorMessage)
            }
            advisorId != null && upcomingAppointments.isNotEmpty() -> {
                AppointmentCardAdvisorList(
                    appointments = upcomingAppointments,
                    farmerNames = farmerNames,
                    farmerImagesUrl = farmerImagesUrl
                )
            }
            else -> {
                Text(
                    text = "No appointments found.",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text("Loading your appointments...")
    }
}

@Composable
fun ErrorView(message: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error: ${message ?: "Unknown error"}", color = MaterialTheme.colorScheme.error)
    }
}
