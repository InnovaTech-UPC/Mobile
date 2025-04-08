package com.example.agrotech.presentation.advisorhome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.presentation.advisorhistory.AppointmentCardAdvisorList

@Composable
fun AdvisorHomeScreen(viewModel: AdvisorHomeViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val appointments = viewModel.appointments
    val advisorId = viewModel.advisorId
    val farmerNames = viewModel.farmerNames
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User ID: ${GlobalVariables.USER_ID}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Token: ${GlobalVariables.TOKEN}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Advisor ID: ${advisorId ?: "Loading..."}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        when {
            isLoading -> {
                LoadingView()
            }
            errorMessage != null -> {
                ErrorView(message = errorMessage)
            }
            advisorId != null && appointments.isNotEmpty() -> {
                AppointmentCardAdvisorList(
                    appointments = appointments,
                    farmerNames = farmerNames,
                    viewModel = viewModel
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
