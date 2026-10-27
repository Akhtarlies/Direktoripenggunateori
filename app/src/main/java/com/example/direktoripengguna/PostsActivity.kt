package com.example.direktoripengguna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class PostsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_NAME = "user_name"
    }

    private lateinit var root: View
    private lateinit var tvStatus: TextView
    private lateinit var tvError: TextView
    private lateinit var rvPosts: RecyclerView
    private lateinit var layoutLoading: View
    private lateinit var layoutError: View
    private lateinit var adapter: PostAdapter

    private val viewModel: PostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        root = findViewById(R.id.main)
        setupHeader(root, findViewById(R.id.header))

        val nama = intent.getStringExtra(EXTRA_USER_NAME).orEmpty()
        findViewById<TextView>(R.id.tvTitle).text = "Postingan $nama"

        tvStatus = findViewById(R.id.tvStatus)
        tvError = findViewById(R.id.tvError)
        rvPosts = findViewById(R.id.rvPosts)
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)

        adapter = PostAdapter { post -> konfirmasiHapus(post) }
        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = adapter

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnRetry).setOnClickListener { viewModel.muat() }
        findViewById<View>(R.id.btnTambah).setOnClickListener { dialogTambah() }

        viewModel.status.observe(this) { tvStatus.text = it }

        viewModel.message.observe(this) { event ->
            event.getIfNotHandled()?.let { snack(it) }
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    rvPosts.visibility = View.GONE
                    layoutError.visibility = View.GONE
                    layoutLoading.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    val bertambah = adapter.itemCount in 1 until state.data.size
                    adapter.setAll(state.data)
                    if (bertambah) rvPosts.scrollToPosition(0)
                    layoutLoading.visibility = View.GONE
                    layoutError.visibility = View.GONE
                    rvPosts.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    tvError.text = state.pesan
                    layoutLoading.visibility = View.GONE
                    rvPosts.visibility = View.GONE
                    layoutError.visibility = View.VISIBLE
                }
            }
        }
    }

    // ---------- POST ----------
    private fun dialogTambah() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_post, null)
        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etIsi = view.findViewById<EditText>(R.id.etIsi)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Post")
            .setView(view)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Kirim", null)
            .create()
        dialog.show()

        // Override agar dialog tidak tertutup jika input kosong
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val judul = etJudul.text.toString().trim()
            val isi = etIsi.text.toString().trim()
            if (judul.isEmpty() || isi.isEmpty()) {
                etJudul.error = if (judul.isEmpty()) "Wajib diisi" else null
                etIsi.error = if (isi.isEmpty()) "Wajib diisi" else null
            } else {
                dialog.dismiss()
                viewModel.tambah(judul, isi)
            }
        }
    }

    // ---------- DELETE ----------
    private fun konfirmasiHapus(post: Post) {
        AlertDialog.Builder(this)
            .setTitle("Hapus postingan?")
            .setMessage(post.title)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ -> viewModel.hapus(post) }
            .show()
    }

    private fun snack(pesan: String) = Snackbar.make(root, pesan, Snackbar.LENGTH_SHORT).show()
}