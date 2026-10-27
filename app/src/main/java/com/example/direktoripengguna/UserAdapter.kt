package com.example.direktoripengguna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    private val items = mutableListOf<User>()

    fun setAll(list: List<User>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvUsername: TextView = v.findViewById(R.id.tvUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = items[position]
        holder.tvName.text = user.name
        holder.tvUsername.text = "@${user.username}"
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = items.size
}
