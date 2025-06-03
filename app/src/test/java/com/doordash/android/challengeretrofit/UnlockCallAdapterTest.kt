package com.doordash.android.challengeretrofit

import com.doordash.android.challengeretrofit.flow.UnlockFlowManager
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
    fun testUnlockFlow() = runTest {
        val job = launch { UnlockFlowManager.waitForUnlock() }
        delay(50)
        UnlockFlowManager.unlock()
        job.join()
    }
}
