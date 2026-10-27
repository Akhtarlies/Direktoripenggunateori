package com.example.direktoripengguna

import retrofit2.Response

// Memisahkan logika jaringan dari UI (Materi 06)
class UserRepository {
    private val api = RetrofitClient.api

    suspend fun ambilUsers(): List<User> = api.getUsers()
    suspend fun ambilPosts(userId: Int): List<Post> = api.getPostsByUser(userId)
    suspend fun buatPost(post: PostBaru): Post = api.buatPost(post)
    suspend fun hapusPost(id: Int): Response<Unit> = api.hapusPost(id)
}
