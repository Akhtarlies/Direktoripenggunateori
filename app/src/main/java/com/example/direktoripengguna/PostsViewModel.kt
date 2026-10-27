package com.example.direktoripengguna

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = UserRepository()
    // Extra dari Intent otomatis tersedia di SavedStateHandle
    private val userId: Int = savedStateHandle.get<Int>(PostsActivity.EXTRA_USER_ID) ?: 1

    private var current: List<Post> = emptyList()

    private val _state = MutableLiveData<UiState<List<Post>>>()
    val state: LiveData<UiState<List<Post>>> = _state

    private val _status = MutableLiveData("Memuat data...")
    val status: LiveData<String> = _status

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    init { muat() }

    // ---------- GET ----------
    fun muat() {
        if (_state.value is UiState.Loading) return
        _state.value = UiState.Loading
        _status.value = "Memuat data..."
        viewModelScope.launch {
            try {
                current = repository.ambilPosts(userId)
                _state.value = UiState.Success(current)
                updateStatus()
            } catch (e: HttpException) {
                gagalMuat("Server error: HTTP ${e.code()}")
            } catch (e: IOException) {
                gagalMuat("Tidak ada koneksi internet")
            }
        }
    }

    // ---------- POST ----------
    fun tambah(judul: String, isi: String) {
        _status.value = "Mengirim postingan..."
        viewModelScope.launch {
            try {
                val hasil = repository.buatPost(PostBaru(judul, isi, userId))
                // API palsu tidak menyimpan, jadi kita tambahkan ke daftar lokal
                current = listOf(Post(userId, hasil.id, judul, isi)) + current
                _state.value = UiState.Success(current)
                pesan("Tersimpan dengan id ${hasil.id}")
            } catch (e: HttpException) {
                pesan("Gagal menyimpan: HTTP ${e.code()}")
            } catch (e: IOException) {
                pesan("Tidak ada koneksi internet")
            }
            updateStatus()
        }
    }

    // ---------- DELETE ----------
    fun hapus(post: Post) {
        _status.value = "Menghapus postingan..."
        viewModelScope.launch {
            try {
                val response = repository.hapusPost(post.id)
                if (response.isSuccessful) {
                    current = current.filter { it !== post }
                    _state.value = UiState.Success(current)
                    pesan("Postingan berhasil dihapus")
                } else {
                    pesan("Gagal menghapus: HTTP ${response.code()}")
                }
            } catch (e: IOException) {
                pesan("Tidak ada koneksi internet")
            }
            updateStatus()
        }
    }

    private fun updateStatus() { _status.value = "${current.size} postingan" }

    private fun gagalMuat(pesan: String) {
        _status.value = "Gagal memuat data"
        _state.value = UiState.Error(pesan)
    }

    private fun pesan(teks: String) { _message.value = Event(teks) }
}