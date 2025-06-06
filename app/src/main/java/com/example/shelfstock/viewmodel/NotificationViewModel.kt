// NotificationViewModel.kt
package com.example.shelfstock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shelfstock.data.Notification
import com.example.shelfstock.data.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allNotifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val unreadNotifications: StateFlow<List<Notification>> = repository.unreadNotifications
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun markAsRead(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.markAsRead(id)

            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = "Error marking notification as read: ${e.localizedMessage ?: "Unknown error"}"
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    fun markAllAsRead() = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.markAllAsRead()

            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = "Error marking all notifications as read: ${e.localizedMessage ?: "Unknown error"}"
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearAllNotifications() = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.deleteAllNotifications()

            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = "Error clearing notifications: ${e.localizedMessage ?: "Unknown error"}"
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

class NotificationViewModelFactory(private val repository: NotificationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}