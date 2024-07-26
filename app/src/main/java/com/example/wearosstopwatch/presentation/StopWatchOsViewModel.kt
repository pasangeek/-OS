package com.example.wearosstopwatch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class StopWatchOsViewModel : ViewModel() {
    // MutableStateFlow to hold the elapsed time in milliseconds
    private val _elapsedTime = MutableStateFlow(0L)

    // MutableStateFlow to hold the state of the timer
    private val _timerState = MutableStateFlow(TimerSate.RESET)

    // Public immutable state flow for the timer state
    val timerSate = _timerState.asStateFlow()

    // Formatter to format the elapsed time in HH:mm:ss:SSS format
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")

    // Public state flow that maps elapsed time to formatted time string
    val stopWatchText = _elapsedTime
        .map { millis ->
            LocalTime.ofNanoOfDay(millis * 1_000_000).format(formatter)

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "00:00:00:000"
        )

    // Observes timerState changes and updates the timer flow accordingly
    init {
        _timerState
            .flatMapLatest { timerSate ->
                getTimerFlow(
                    isRunning = timerSate == TimerSate.RUNNING
                )
            }
    }

    /**
     * Creates a flow that emits the time differences when the timer is running.
     *
     * @param isRunning Indicates if the timer is running.
     * @return A flow emitting the elapsed time in milliseconds.
     */
    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while (isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if (currentMillis > startMillis) {
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }

}