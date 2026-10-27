package com.example.direktoripengguna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val onDelete: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    private val items = mutableListOf<Post>()

    fun setAll(list: List<Post>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvBody: TextView = v.findViewById(R.id.tvBody)
        val btnHapus: TextView = v.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = items[position]
        holder.tvTitle.text = post.title
        holder.tvBody.text = post.body
        holder.btnHapus.setOnClickListener { onDelete(post) }
    }

    override fun getItemCount() = items.size
}