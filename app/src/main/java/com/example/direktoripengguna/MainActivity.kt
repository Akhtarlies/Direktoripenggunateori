package com.example.direktoripengguna

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvError: TextView
    private lateinit var rvUsers: RecyclerView
    private lateinit var layoutLoading: View
    private lateinit var layoutError: View
    private lateinit var adapter: UserAdapter

    // ViewModel bertahan saat layar diputar
    private val viewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupHeader(findViewById(R.id.main), findViewById(R.id.tvTitle))

        tvStatus = findViewById(R.id.tvStatus)
        tvError = findViewById(R.id.tvError)
        rvUsers = findViewById(R.id.rvUsers)
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)

        adapter = UserAdapter { user ->
            startActivity(
                Intent(this, PostsActivity::class.java)
                    .putExtra(PostsActivity.EXTRA_USER_ID, user.id)
                    .putExtra(PostsActivity.EXTRA_USER_NAME, user.name)
            )
        }
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        findViewById<View>(R.id.btnRetry).setOnClickListener { viewModel.muat() }

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    tvStatus.text = "Memuat data..."
                    rvUsers.visibility = View.GONE
                    layoutError.visibility = View.GONE
                    layoutLoading.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    adapter.setAll(state.data)
                    tvStatus.text = "${state.data.size} pengguna dimuat"
                    layoutLoading.visibility = View.GONE
                    layoutError.visibility = View.GONE
                    rvUsers.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    tvStatus.text = "Gagal memuat data"
                    tvError.text = state.pesan
                    layoutLoading.visibility = View.GONE
                    rvUsers.visibility = View.GONE
                    layoutError.visibility = View.VISIBLE
                }
            }
        }
    }
}