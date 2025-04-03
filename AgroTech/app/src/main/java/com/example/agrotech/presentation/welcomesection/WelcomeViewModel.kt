package com.example.agrotech.presentation.welcomesection

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.common.Resource
import com.example.agrotech.common.Routes
import com.example.agrotech.common.UIState
import com.example.agrotech.data.repository.authentication.AuthenticationRepository
import kotlinx.coroutines.launch


class WelcomeViewModel(
    private val navController: NavController,
    private val authenticationRepository: AuthenticationRepository
): ViewModel()  {

    private val _state = mutableStateOf(UIState<Unit>())
    val state: State<UIState<Unit>> get() = _state

    fun checkUser() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            when (val result = authenticationRepository.getUser()) {
                is Resource.Success -> {
                    GlobalVariables.TOKEN = result.data?.token ?: ""
                    GlobalVariables.USER_ID = result.data?.id ?: 0
                    if (GlobalVariables.TOKEN.isNotBlank() && GlobalVariables.USER_ID != 0L) {
                        goToFarmerHomeScreen()
                    }
                    _state.value = UIState(message = "Error al recuperar usuario")
                }
                is Resource.Error -> {
                    // No se encontró usuario
                    _state.value = UIState(isLoading = false)
                }
            }
        }
    }

    fun goToLoginScreen() {
        navController.navigate(Routes.SignIn.route)
    }

    private fun goToFarmerHomeScreen() {
        navController.navigate(Routes.FarmerHome.route)
    }

    fun goToSignUpScreen() {
        navController.navigate(Routes.SignUp.route)
    }

}