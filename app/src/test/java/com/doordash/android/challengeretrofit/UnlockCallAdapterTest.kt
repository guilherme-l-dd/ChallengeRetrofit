package com.doordash.android.challengeretrofit

import com.doordash.android.challengeretrofit.flow.ChallengeFlowManager
import com.doordash.android.challengeretrofit.network.MockInterceptor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import org.junit.Assert.*

class UnlockCallAdapterTest {

    @Test
    fun testMockInterceptor() = runTest {
        val client = OkHttpClient.Builder()
            .addInterceptor(MockInterceptor())
            .build()

        val response = client.newCall(
            Request.Builder()
                .url("https://api.example.com/test-endpoint")
                .build()
        ).execute()

        assertEquals(423, response.code)
        assertTrue(response.body?.string()?.contains("Resource locked") == true)
    }

    @Test
    fun testChallengeFlow() = runTest {
        val job = launch { ChallengeFlowManager.waitForChallenge() }
        delay(50)
        ChallengeFlowManager.completeChallenge()
        job.join()
    }
}
