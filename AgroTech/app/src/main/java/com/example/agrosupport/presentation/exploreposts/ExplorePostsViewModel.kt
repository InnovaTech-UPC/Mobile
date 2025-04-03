package com.example.agrosupport.presentation.exploreposts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agrosupport.common.GlobalVariables
import com.example.agrosupport.common.Resource
import com.example.agrosupport.common.Routes
import com.example.agrosupport.common.UIState
import com.example.agrosupport.data.repository.advisor.AdvisorRepository
import com.example.agrosupport.data.repository.post.PostRepository
import com.example.agrosupport.data.repository.profile.ProfileRepository
import kotlinx.coroutines.launch

class ExplorePostsViewModel(private val navController: NavController,
                            private val postRepository: PostRepository,
                            private val profileRepository: ProfileRepository,
                            private val advisorRepository: AdvisorRepository
) : ViewModel() {
    private val _state = mutableStateOf(UIState<List<PostCard>>())
    val state: State<UIState<List<PostCard>>> get() = _state

    fun goBack() {
        navController.popBackStack()
    }

    fun getPostList() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = postRepository.getPosts(GlobalVariables.TOKEN)
            if (result is Resource.Success) {
                val posts = result.data ?: run {
                    _state.value = UIState(message = "No se encontraron posts")
                    return@launch
                }

                val postCards = posts.map { post ->
                    val advisorResult = advisorRepository.searchAdvisorByAdvisorId(post.advisorId, GlobalVariables.TOKEN)
                    val advisorProfile = if (advisorResult is Resource.Success) {
                        val advisor = advisorResult.data
                        if (advisor != null) {
                            val profileResult = profileRepository.searchProfile(advisor.userId, GlobalVariables.TOKEN)
                            if (profileResult is Resource.Success) profileResult.data else null
                        } else null
                    } else null

                    val advisorName = advisorProfile?.let { "${it.firstName} ${it.lastName}" } ?: "Anónimo"
                    val advisorPhoto = advisorProfile?.photo ?: ""

                    PostCard(
                        id = post.id,
                        title = post.title,
                        description = post.description,
                        image = post.image,
                        advisorId = post.advisorId,
                        advisorName = advisorName,
                        advisorPhoto = advisorPhoto
                    )
                }
                _state.value = UIState(data = postCards)
            }
            else {
                _state.value = UIState(message = "Error al intentar obtener los posts")
            }
        }
    }

    fun goToAdvisorDetail(advisorId: Long) {
        navController.navigate(Routes.AdvisorDetail.route + "/$advisorId")
    }

}