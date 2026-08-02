package com.aiforall.app.presentation.screens.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiforall.app.domain.model.ModerationStatus
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.repository.ModerationRepository
import com.aiforall.app.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val moderationRepository: ModerationRepository
) : ViewModel() {

    val reportedPosts: StateFlow<List<Post>> = postRepository.observeReportedPosts()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Removes just this one post, leaves the author's other posts and status untouched. */
    fun removePost(postId: String) {
        viewModelScope.launch { postRepository.hidePost(postId) }
    }

    /** Clears reports without any action — for a report that turns out to be unfounded. */
    fun dismissReports(postId: String) {
        viewModelScope.launch { postRepository.dismissReports(postId) }
    }

    /** Bumps the author one step up the warn -> restrict -> shadowban -> ban ladder. */
    fun escalateAuthor(authorId: String) {
        viewModelScope.launch { moderationRepository.escalate(authorId) }
    }

    /** Jumps straight to a specific level (e.g. immediate ban for a severe violation). */
    fun setAuthorStatus(authorId: String, status: ModerationStatus) {
        viewModelScope.launch { moderationRepository.setModerationStatus(authorId, status) }
    }
}
