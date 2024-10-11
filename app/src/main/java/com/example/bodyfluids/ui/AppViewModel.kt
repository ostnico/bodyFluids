package com.example.bodyfluids.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = AppUiState()
    }

    fun updateResult(pair: Pair<String, String>) {
        if (pair.second.toIntOrNull() == null && pair.second.toDoubleOrNull() == null) return

        when (pair.first) {
            "saliva15" -> {
                _uiState.update { currentState ->
                    currentState.copy(saliva15 = pair.second.toDouble())
                }
            }
            "saliva5" -> {
                _uiState.update { currentState ->
                    currentState.copy(saliva5 = pair.second.toDouble())
                }
            }
            "rightEye" -> {
                _uiState.update { currentState ->
                    currentState.copy(rightEye = pair.second.toInt())
                }
            }
            "leftEye" -> {
                _uiState.update { currentState ->
                    currentState.copy(leftEye = pair.second.toInt())
                }
            }
        }
    }
}