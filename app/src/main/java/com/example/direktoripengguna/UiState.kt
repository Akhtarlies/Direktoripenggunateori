package com.example.direktoripengguna

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val pesan: String) : UiState<Nothing>
}

/** Pesan sekali tampil (Snackbar) supaya tidak muncul lagi saat layar diputar. */
class Event<out T>(private val content: T) {
    private var handled = false
    fun getIfNotHandled(): T? =
        if (handled) null else { handled = true; content }
}