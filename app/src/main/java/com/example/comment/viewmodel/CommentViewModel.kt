package com.example.comment.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comment.model.Comment
import com.example.comment.network.CommentApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommentViewModel : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(CommentApiService::class.java)

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    init {
        fetchComments(1)
    }

    fun fetchComments(postId: Int) {
        viewModelScope.launch {
            try {
                _comments.value = apiService.getComments(postId)
            } catch (e: Exception) {
                // Handle error (logging, display error state, etc.)
            }
        }
    }
}