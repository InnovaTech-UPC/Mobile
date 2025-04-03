package com.example.agrosupport.presentation.createprofilefarmer

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrosupport.common.GlobalVariables
import com.example.agrosupport.common.Resource
import com.example.agrosupport.common.Routes
import com.example.agrosupport.common.UIState
import com.example.agrosupport.data.repository.profile.CloudStorageRepository
import com.example.agrosupport.data.repository.profile.ProfileRepository
 import com.example.agrosupport.domain.profile.Profile
import com.example.agrosupport.presentation.createaccountfarmer.CreateAccountFarmerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateProfileFarmerViewModel(
    private val navController: NavController,
    private val profileRepository: ProfileRepository,
    private val createAccountFarmerViewModel: CreateAccountFarmerViewModel,
    private val cloudStorageRepository: CloudStorageRepository
) : ViewModel() {

    // Variables de estado para los campos de texto
    var photoUrl = mutableStateOf("")
        private set
    var description = mutableStateOf("")
        private set

    private val _state = mutableStateOf(UIState<Profile>())
    val state: State<UIState<Profile>> get() = _state

    // Variable de estado para el mensaje del Snackbar
    private val _snackbarMessage = mutableStateOf<String?>(null)
    val snackbarMessage: State<String?> get() = _snackbarMessage

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun goToLoginScreen() {
        navController.navigate(Routes.SignIn.route)
    }

    private fun goToConfirmationAccountFarmerScreen() {
        navController.navigate(Routes.ConfirmCreationAccountFarmer.route)
    }

    fun createProfile() {
        viewModelScope.launch {
            val token = GlobalVariables.TOKEN
            if (token.isBlank()) {
                _state.value = UIState(message = "Un token es requerido")
                _snackbarMessage.value = "Un token es requerido"
                return@launch
            }

            _state.value = UIState(isLoading = true)
            val profile = createAccountFarmerViewModel.getProfile().copy(
                description = description.value,
                photo = photoUrl.value
            )
            val result = profileRepository.createProfileFarmer(token, profile)
            withContext(Dispatchers.Main) {
                if (result is Resource.Success) {
                    _state.value = UIState(data = result.data)
                    goToConfirmationAccountFarmerScreen()
                } else {
                    _state.value = UIState(message = result.message ?: "Error al crear perfil")
                    _snackbarMessage.value = result.message ?: "Error al crear perfil"
                }
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            try {
                val filename = imageUri.lastPathSegment ?: "default_image_name"
                val imageUrl = cloudStorageRepository.uploadFile(filename, imageUri)
                photoUrl.value = imageUrl
                _state.value = UIState(isLoading = false)
            } catch (e: Exception) {
                _state.value = UIState(message = "Error uploading image: ${e.message}")
            }
        }
    }

}