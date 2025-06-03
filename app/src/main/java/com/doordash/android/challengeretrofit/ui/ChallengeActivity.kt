package com.doordash.android.challengeretrofit.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.doordash.android.challengeretrofit.R
import com.doordash.android.challengeretrofit.flow.ChallengeFlowManager

class ChallengeActivity : ComponentActivity() {
    
    private lateinit var btnUnlock: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        btnUnlock = findViewById(R.id.btnUnlock)
    }
    
    private fun setupClickListeners() {
        btnUnlock.setOnClickListener {
            // Publish value to the flow to complete the challenge and unlock the CallAdapter execution
            ChallengeFlowManager.completeChallenge()

            // Finish this activity and go back to MainActivity
            finish()
        }
    }
}
