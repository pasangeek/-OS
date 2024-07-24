package com.example.wearosstopwatch.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StopWatchOsViewModel : ViewModel() {
    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerSate.RESET)
    val timerSate = _timerState.asStateFlow()
}