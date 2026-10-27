package com.example.direktoripengguna

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsersViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _state = MutableLiveData<UiState<List<User>>>()
    val state: LiveData<UiState<List<User>>> = _state

    // Dijalankan sekali saat ViewModel dibuat, tidak diulang saat layar diputar
    init { muat() }

    fun muat() {
        if (_state.value is UiState.Loading) return
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                _state.value = UiState.Success(repository.ambilUsers())
            } catch (e: HttpException) {
                _state.value = UiState.Error("Server error: HTTP ${e.code()}")
            } catch (e: IOException) {
                _state.value = UiState.Error("Tidak ada koneksi internet")
            }
        }
    }
}