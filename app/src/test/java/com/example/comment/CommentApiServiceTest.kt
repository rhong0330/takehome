package com.example.comment

import com.example.comment.model.Comment
import com.example.comment.network.CommentApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CommentApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CommentApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())  // Add support for Kotlin classes
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        apiService = retrofit.create(CommentApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getComments should return a list of comments on success`() = runTest {
        val mockResponse = MockResponse()
            .setBody("""
                [
                    {"postId": 1, "id": 1, "name": "Test Name", "email": "test@example.com", "body": "Test body"}
                ]
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = apiService.getComments(1)

        // Check if the response is successful
        assertEquals(true, response.isSuccessful)

        // Extract the body and perform assertions
        val comments = response.body()
        assertNotNull(comments)
        assertEquals(1, comments!!.size)
        assertEquals("Test Name", comments.first().name)
    }

    @Test
    fun `getComments should handle error response`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        try {
            val response = apiService.getComments(1)
            assertEquals(false, response.isSuccessful)
        } catch (e: Exception) {
            // Handle exception if necessary
        }
    }
}