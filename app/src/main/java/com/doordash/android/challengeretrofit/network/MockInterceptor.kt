package com.doordash.android.challengeretrofit.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Mock 423 response for our test endpoint
        if (request.url.encodedPath.contains("test-endpoint")) {
            val responseBody = """
                {
                    "message": "Resource locked - please complete verification",
                    "status": "locked"
                }
            """.trimIndent()
            
            return Response.Builder()
                .code(423)
                .message("Locked")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(responseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }
        
        return chain.proceed(request)
    }
}
