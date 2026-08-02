package com.aiforall.app.presentation.screens.community

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostMediaType
import com.aiforall.app.domain.model.PostType
import com.aiforall.app.domain.repository.AuthRepository
import com.aiforall.app.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // The feed query itself depends on WHO is viewing (shadowbanned
    // authors' posts stay visible only to themselves) — re-subscribe
    // whenever the signed-in user changes rather than fixing the uid once.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val feed = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else postRepository.observeFeedPosts(user.uid)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        feed.onEach { _posts.value = it }.launchIn(viewModelScope)
    }

    fun submitPost(
        title: String,
        body: String,
        type: PostType,
        linkUrl: String?,
        authorId: String,
        authorName: String,
        mediaUris: List<Uri> = emptyList(),
        mediaType: PostMediaType = PostMediaType.NONE
    ) {
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
                mediaUris = mediaUris,
                mediaType = mediaType
            )
        }
    }

    fun reportPost(postId: String, userId: String, reason: String) {
        viewModelScope.launch { postRepository.reportPost(postId, userId, reason) }
    }
}
