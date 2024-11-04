package com.example.comment.network

import com.example.comment.model.Comment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CommentApiService {
    @GET("posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Int): Response<List<Comment>>
}