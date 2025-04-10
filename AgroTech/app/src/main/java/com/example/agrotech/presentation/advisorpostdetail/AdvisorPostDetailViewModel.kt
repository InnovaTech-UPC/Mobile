package com.example.agrotech.presentation.advisorpostdetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrotech.common.GlobalVariables
import com.example.agrotech.common.Resource
import com.example.agrotech.common.UIState
import com.example.agrotech.data.repository.post.PostRepository
import com.example.agrotech.domain.post.Post
import kotlinx.coroutines.launch

class AdvisorPostDetailViewModel(
    private val navController: NavController, private val postRepository: PostRepository
): ViewModel() {
    private val _state = mutableStateOf(UIState<Post>())
    val state: State<UIState<Post>> get() = _state

    private val _title = mutableStateOf("")
    val title: State<String> get() = _title

    private val _description = mutableStateOf("")
    val description: State<String> get() = _description

    private val _image = mutableStateOf("")
    val image: State<String> get() = _image

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> get() = _expanded

    fun setExpanded(value: Boolean) {
        _expanded.value = value
    }

    fun goBack() {
        navController.popBackStack()
    }

    fun getPost(postId: Long) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            when (val result = postRepository.getPostById(GlobalVariables.TOKEN, postId)) {
                is Resource.Success -> {
                    val post = result.data ?: run {
                        _state.value = UIState(message = "Error al obtener la publicación 1")
                        return@launch
                    }
                    _state.value = UIState(data = post)
                    _title.value = post.title
                    _description.value = post.description
                    _image.value = post.image
                }
                is Resource.Error -> {
                    _state.value = UIState(message = "Error al obtener la publicación 2")
                }
            }
        }
    }

    fun onUpdateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun onUpdateDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun updatePost(postId: Long) {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val post = Post(
                id = postId,
                advisorId = 0,
                title = _title.value,
                description = _description.value,
                image = _image.value
            )
            when (postRepository.updatePost(GlobalVariables.TOKEN, post)) {
                is Resource.Success -> {
                    _state.value = UIState(data = post)
                    navController.popBackStack()
                }
                is Resource.Error -> {
                    _state.value = UIState(message = "Error al actualizar la publicación")
                }
            }
        }
    }

    fun deletePost(postId: Long){
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            when (postRepository.deletePost(GlobalVariables.TOKEN, postId)) {
                is Resource.Success -> {
                    _state.value = UIState(data = null)
                    _expanded.value = false
                    navController.popBackStack()
                }
                is Resource.Error -> {
                    _state.value = UIState(message = "Error al eliminar la publicación")
                }
            }
        }
    }

}