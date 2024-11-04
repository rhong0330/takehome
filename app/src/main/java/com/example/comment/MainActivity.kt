package com.example.comment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.comment.network.CommentApiService
import com.example.comment.ui.CommentListScreen
import com.example.comment.ui.theme.CommentTheme
import com.example.comment.viewmodel.CommentViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create ApiService instance
        val apiService = retrofit.create(CommentApiService::class.java)

        // Instantiate ViewModel and pass ApiService
        viewModel = CommentViewModel(apiService)

        setContent {
            CommentTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CommentListScreen(viewModel)
                }
            }
        }
    }
}