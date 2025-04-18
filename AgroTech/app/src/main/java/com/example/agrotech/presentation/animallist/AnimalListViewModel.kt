package com.example.agrotech.presentation.animallist

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.agrotech.data.repository.animal.AnimalRepository

class AnimalListViewModel(
    private val navController: NavController,
    private val animalRepository: AnimalRepository,
): ViewModel() {

}
