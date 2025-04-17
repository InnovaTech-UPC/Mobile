package com.example.agrotech.presentation.advisoravailabledates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.agrotech.domain.appointment.AvailableDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisorAvailableDatesScreen(
    viewModel: AdvisorAvailableDatesViewModel
) {
    val availableDates by viewModel.availableDates.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var availableDateInput by remember { mutableStateOf(TextFieldValue("")) }
    var startTimeInput by remember { mutableStateOf(TextFieldValue("")) }
    var endTimeInput by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.initScreen()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Available Dates") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(availableDates ?: emptyList()) { date ->
                    Text(text = date.toString(), modifier = Modifier.padding(8.dp))
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create Available Date") },
                text = {
                    Column {
                        Text("Enter Available Date (YYYY-MM-DD):")
                        BasicTextField(
                            value = availableDateInput,
                            onValueChange = { availableDateInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Enter Start Time (HH:mm):")
                        BasicTextField(
                            value = startTimeInput,
                            onValueChange = { startTimeInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Enter End Time (HH:mm):")
                        BasicTextField(
                            value = endTimeInput,
                            onValueChange = { endTimeInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val advisorId = viewModel.advisorId
                        if (advisorId != null) {
                            val newDate = AvailableDate(
                                id = 0L,
                                advisorId = advisorId,
                                availableDate = availableDateInput.text,
                                startTime = startTimeInput.text,
                                endTime = endTimeInput.text
                            )
                            viewModel.createAvailableDate(newDate)
                            showDialog = false
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
