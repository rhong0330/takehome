package com.example.comment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comment.model.Comment
import com.example.comment.network.CommentApiService
import kotlinx.coroutines.launch

class CommentViewModel(private val apiService: CommentApiService) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment>>(emptyList())

    val comments: LiveData<List<Comment>> get() = _comments

    init {
        fetchComments(1)
    }

    fun fetchComments(postId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getComments(postId)
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: emptyList() // Handle null body
                } else {
                    _comments.value = emptyList() // Handle non-successful response
                    // Optionally, handle the error case (e.g., log the error or set an error state)
                }
            } catch (e: Exception) {
                // Handle error (logging, display error state, etc.)
            }
        }
    }
}