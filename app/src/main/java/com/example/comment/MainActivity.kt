package com.example.comment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.comment.ui.CommentListScreen
import com.example.comment.ui.theme.CommentTheme
import com.example.comment.viewmodel.CommentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommentTheme {
                val viewModel: CommentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
                Surface(modifier = Modifier.fillMaxSize()) {
                    CommentListScreen(viewModel)
                }
            }
        }
    }
}