package com.nikidas.demo.githubviewer.ui.file

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nikidas.demo.githubviewer.R

// 简单数据类，type: "file" or "dir"
data class RepoFileItem(val name: String, val path: String, val type: String)

class FileListAdapter(
    private val onItemClick: (RepoFileItem) -> Unit
) : ListAdapter<RepoFileItem, FileListAdapter.FileViewHolder>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RepoFileItem>() {
            override fun areItemsTheSame(oldItem: RepoFileItem, newItem: RepoFileItem) = oldItem.path == newItem.path
            override fun areContentsTheSame(oldItem: RepoFileItem, newItem: RepoFileItem) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file_entry, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: TextView = view.findViewById(R.id.ivIcon)
        private val name: TextView = view.findViewById(R.id.tvTitle)
        fun bind(item: RepoFileItem) {
            icon.text = if (item.type == "dir") "📁" else "📄"
            name.text = item.name
            itemView.setOnClickListener {
                if (item.type == "file") {
                    val context = itemView.context
                    val intent = Intent(context, FilePreviewActivity::class.java)
                    intent.putExtra("repo_owner", (context as? androidx.appcompat.app.AppCompatActivity)?.intent?.getStringExtra("repo_owner") ?: "")
                    intent.putExtra("repo_name", (context as? androidx.appcompat.app.AppCompatActivity)?.intent?.getStringExtra("repo_name") ?: "")
                    intent.putExtra("path", item.path)
                    intent.putExtra("name", item.name)
                    context.startActivity(intent)
                } else {
                    onItemClick(item)
                }
            }
        }
    }
} 