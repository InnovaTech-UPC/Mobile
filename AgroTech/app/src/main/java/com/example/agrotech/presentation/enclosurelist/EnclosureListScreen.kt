package com.example.agrotech.presentation.enclosurelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agrotech.domain.enclosure.Enclosure

@Composable
fun EnclosureListScreen(
    viewModel: EnclosureListViewModel
) {
    val state = viewModel.state
    val showDialog = viewModel.showDialog
    val name = viewModel.name
    val capacity = viewModel.capacity
    val type = viewModel.type

    LaunchedEffect(Unit) {
        viewModel.getEnclosures()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showDialog() }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.goBack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Mis Recintos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.size(48.dp))
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.data.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message.ifEmpty { "No se encontraron recintos." },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data.size) { index ->
                            val enclosure = state.data[index]
                            EnclosureListItem(
                                enclosure = enclosure,
                                onClick = { viewModel.goToAnimals(enclosure.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDialog() },
                title = { Text("Añadir Recinto") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.setName(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = capacity.toString(),
                            onValueChange = { viewModel.setCapacity(it.toIntOrNull() ?: 0) },
                            label = { Text("Capacidad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = type,
                            onValueChange = { viewModel.setType(it) },
                            label = { Text("Tipo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createEnclosure()
                        viewModel.hideDialog()
                    }) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.hideDialog() }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun EnclosureListItem(
    enclosure: Enclosure,
    onClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(enclosure.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏷️ ${enclosure.name}", fontWeight = FontWeight.Bold)
            Text("📦 Capacidad: ${enclosure.capacity}")
            Text("📌 Tipo: ${enclosure.type}")
        }
    }
}