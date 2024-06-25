package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val _notificationsWithProgressBar = MutableStateFlow<List<String>>(emptyList())
    val notificationsWithProgressBar: StateFlow<List<String>> = _notificationsWithProgressBar

    fun addNotification(notification: String) {
        viewModelScope.launch {
            val currentList = _notificationsWithProgressBar.value.toMutableList()
            currentList.add(notification)
            _notificationsWithProgressBar.value = currentList
        }
    }

    fun removeNotification(notification: String) {
        viewModelScope.launch {
            val currentList = _notificationsWithProgressBar.value.toMutableList()
            currentList.remove(notification)
            _notificationsWithProgressBar.value = currentList
        }
    }
}