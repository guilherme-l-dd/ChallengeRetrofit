package com.doordash.android.challengeretrofit.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.doordash.android.challengeretrofit.R
import com.doordash.android.challengeretrofit.flow.UnlockFlowManager

class SecondActivity : ComponentActivity() {
    
    private lateinit var btnUnlock: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        btnUnlock = findViewById(R.id.btnUnlock)
    }
    
    private fun setupClickListeners() {
        btnUnlock.setOnClickListener {
            // Publish value to the flow to unlock the CallAdapter execution
            UnlockFlowManager.unlock()
            
            // Finish this activity and go back to MainActivity
            finish()
        }
    }
}
