package com.doordash.android.challengeretrofit.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first

object ChallengeFlowManager {
    private val _navigationFlow = MutableSharedFlow<Unit>(replay = 1)
    val navigationFlow: SharedFlow<Unit> = _navigationFlow.asSharedFlow()

    private val _uiUpdateFlow = MutableSharedFlow<Unit>(replay = 1)
    val uiUpdateFlow: SharedFlow<Unit> = _uiUpdateFlow.asSharedFlow()

    private val _challengeChannel = Channel<Unit>(Channel.UNLIMITED)
    private val challengeFlow = _challengeChannel.receiveAsFlow()

    fun completeChallenge() {
        _challengeChannel.trySend(Unit)
        _uiUpdateFlow.tryEmit(Unit)
    }

    suspend fun waitForChallenge() {
        _uiUpdateFlow.resetReplayCache()
        _navigationFlow.tryEmit(Unit)
        challengeFlow.first()
    }
}
