package com.doordash.android.challengeretrofit.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first

object UnlockFlowManager {
    private val _navigationFlow = MutableSharedFlow<Unit>(replay = 1)
    val navigationFlow: SharedFlow<Unit> = _navigationFlow.asSharedFlow()

    private val _uiUpdateFlow = MutableSharedFlow<Unit>(replay = 1)
    val uiUpdateFlow: SharedFlow<Unit> = _uiUpdateFlow.asSharedFlow()

    private val _unlockChannel = Channel<Unit>(Channel.UNLIMITED)
    private val unlockFlow = _unlockChannel.receiveAsFlow()

    fun unlock() {
        _unlockChannel.trySend(Unit)
        _uiUpdateFlow.tryEmit(Unit)
    }

    suspend fun waitForUnlock() {
        _uiUpdateFlow.resetReplayCache()
        _navigationFlow.tryEmit(Unit)
        unlockFlow.first()
    }
}
