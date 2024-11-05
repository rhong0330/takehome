package com.example.comment.ui


import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.comment.viewmodel.CommentViewModel
import com.example.comment.model.Comment
import com.example.comment.R

@Composable
fun CommentListScreen(viewModel: CommentViewModel) {
    var postId by remember { mutableStateOf(1) }
    var inputPostId by remember { mutableStateOf("") }
    val comments by viewModel.comments.observeAsState(initial = emptyList())

    //val filteredComments = comments.filter { it.id % 2 == 0 }

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        items(comments) { comment ->
            CommentItem(comment)
        }

        item{
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    if (postId > 1) postId--
                    viewModel.fetchComments(postId)
                }) {
                    Text("Previous")
                }
                Button(onClick = {
                    postId++
                    viewModel.fetchComments(postId)
                }) {
                    Text("Next")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputPostId,
                onValueChange = { inputPostId = it },
                label = { Text("Enter Post ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                inputPostId.toIntOrNull()?.let {
                    postId = it
                    viewModel.fetchComments(postId)
                }
            }) {
                Text("Search Post ID")
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") }
                .padding(8.dp)
        ) {
            selectedImageUri?.let { uri ->
                val bitmap = remember(uri) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Profile Photo for ${comment.name}",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } ?: Image(
                painter = painterResource(id = R.drawable.default_profile),
                contentDescription = "Default Profile Photo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp)
        ) {
            Text(text = "Name: ${comment.name}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Email: ${comment.email}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.clickable {
                    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                    val clip = android.content.ClipData.newPlainText("Email", comment.email)
                    clipboard.setPrimaryClip(clip)
                }
            )
            Text(text = "id: ${comment.id}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Body: ${comment.body}", style = MaterialTheme.typography.bodySmall)
        }
    }
}