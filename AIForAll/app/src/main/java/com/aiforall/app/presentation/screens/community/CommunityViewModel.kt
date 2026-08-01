package com.aiforall.app.presentation.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostType
import com.aiforall.app.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        postRepository.observeApprovedPosts()
            .onEach { _posts.value = it }
            .launchIn(viewModelScope)
    }

    fun submitPost(title: String, body: String, type: PostType, linkUrl: String?, authorId: String, authorName: String) {
        viewModelScope.launch {
            postRepository.createPost(
                Post(
                    authorId = authorId,
                    authorName = authorName,
                    type = type,
                    title = title,
                    body = body,
                    linkUrl = linkUrl
                ),
                imageBytes = null
            )
        }
    }
}
