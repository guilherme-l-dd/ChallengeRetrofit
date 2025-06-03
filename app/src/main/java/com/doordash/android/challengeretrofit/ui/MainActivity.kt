package com.doordash.android.challengeretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.doordash.android.challengeretrofit.R
import com.doordash.android.challengeretrofit.data.ApiResponse
import com.doordash.android.challengeretrofit.flow.ChallengeFlowManager
import com.doordash.android.challengeretrofit.network.NetworkClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private lateinit var btnCallApi: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        observeFlows()
    }

    private fun initViews() {
        btnCallApi = findViewById(R.id.btnCallApi)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupClickListeners() {
        btnCallApi.setOnClickListener {
            callApi()
        }
    }
    
    private fun callApi() {
        btnCallApi.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvStatus.text = "Calling API..."

        val apiService = NetworkClient.createApiService()
        val call = apiService.getTestData()

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    tvStatus.text = "API call successful: ${response.body()?.message ?: "No message"}"
                } else {
                    tvStatus.text = "API call failed with code: ${response.code()}"
                }
                btnCallApi.isEnabled = true
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                tvStatus.text = "API call failed: ${t.message}"
                btnCallApi.isEnabled = true
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun observeFlows() {
        lifecycleScope.launch {
            ChallengeFlowManager.navigationFlow.collect {
                tvStatus.text = "Challenge required - launching challenge screen..."
                startActivity(Intent(this@MainActivity, ChallengeActivity::class.java))
            }
        }

        lifecycleScope.launch {
            ChallengeFlowManager.uiUpdateFlow.collect {
                tvStatus.text = "API call successful: Resource unlocked after challenge"
                btnCallApi.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }
}
