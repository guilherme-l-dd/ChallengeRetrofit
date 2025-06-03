package com.doordash.android.challengeretrofit.network

import com.doordash.android.challengeretrofit.flow.ChallengeFlowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ChallengeCallAdapterFactory : CallAdapter.Factory() {

    // Create a scope for network operations with SupervisorJob
    private val networkScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Handle Call<T> types
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
        return ChallengeCallAdapter<Any>(responseType, networkScope)
    }
    
    private class ChallengeCallAdapter<T>(
        private val responseType: Type,
        private val networkScope: CoroutineScope
    ) : CallAdapter<T, Call<T>> {
        
        override fun responseType(): Type = responseType
        
        override fun adapt(call: Call<T>): Call<T> {
            return ChallengeCall(call, networkScope)
        }
    }
    
    private class ChallengeCall<T>(
        private val delegate: Call<T>,
        private val networkScope: CoroutineScope
    ) : Call<T> by delegate {
        
        override fun execute(): retrofit2.Response<T> {
            val response = delegate.execute()

            if (response.code() == 423) {
                // For execute(), we can't handle async flow properly
                // This should ideally not be used with the challenge pattern
                // Return the 423 response as-is
                return response
            }

            return response
        }
        
        override fun enqueue(callback: retrofit2.Callback<T>) {
            delegate.enqueue(object : retrofit2.Callback<T> {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                    if (response.code() == 423) {
                        // Wait for challenge completion in a coroutine
                        networkScope.launch {
                            ChallengeFlowManager.waitForChallenge()
                            // After challenge completion, call success
                            callback.onResponse(
                                call,
                                retrofit2.Response.success(
                                    response.body() ?: return@launch
                                )
                            )
                        }
                    } else {
                        callback.onResponse(call, response)
                    }
                }
                
                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onFailure(call, t)
                }
            })
        }
        
        override fun clone(): Call<T> = ChallengeCall(delegate.clone(), networkScope)
    }
}
