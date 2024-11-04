package com.example.comment

import androidx.lifecycle.Observer
import com.example.comment.model.Comment
import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.comment.viewmodel.CommentViewModel
import com.example.comment.network.CommentApiService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CommentViewModelTest {

    @MockK
    private lateinit var mockApiService: CommentApiService

    private lateinit var viewModel: CommentViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this) // Initialize MockK
        Dispatchers.setMain(testDispatcher)
        viewModel = CommentViewModel(mockApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher to the original
    }

    @Test
    fun `initial comments state should be empty`() = runTest {
        assertEquals(emptyList<Comment>(), viewModel.comments.value)
    }

    @Test
    fun `fetchComments should update comments state`() = runTest {
        val mockComments = listOf(Comment(1, 1, "Test Name", "test@example.com", "Test body"))
        val mockResponse = Response.success(mockComments)
        coEvery { mockApiService.getComments(any()) } returns mockResponse

        viewModel.fetchComments(1)

        // Ensure that the coroutine completes before assertions
        advanceUntilIdle()

        val observer = Observer<List<Comment>> {}
        try {
            viewModel.comments.observeForever(observer)
            assertEquals(mockComments, viewModel.comments.value)
        } finally {
            viewModel.comments.removeObserver(observer)
        }
    }

    @Test
    fun `fetchComments should handle failure gracefully`() = runTest {
        coEvery { mockApiService.getComments(any()) } throws Exception("Network error")

        viewModel.fetchComments(1)
        advanceUntilIdle() // Ensure coroutines complete

        val observer = Observer<List<Comment>> {}
        try {
            viewModel.comments.observeForever(observer)
            assertEquals(emptyList<Comment>(), viewModel.comments.value)
        } finally {
            viewModel.comments.removeObserver(observer)
        }
    }
}