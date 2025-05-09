package com.nikidas.demo.githubviewer.ui.issue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.Issue
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class IssueAdapter : ListAdapter<Issue, IssueAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(issue: Issue) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val tvComments = itemView.findViewById<TextView>(R.id.tvComments)
            tvTitle.text = issue.title
            tvNumber.text = "#${issue.number}"
            tvDate.text = getRelativeTime(issue.created_at)
            tvComments.text = issue.comments.toString()
        }
        private fun getRelativeTime(dateStr: String): String {
            // dateStr: 2024-05-10T15:00:00Z
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return try {
                val time = sdf.parse(dateStr)?.time ?: return ""
                val now = System.currentTimeMillis()
                val diff = now - time
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                return when {
                    days < 1 -> "today"
                    days < 30 -> "${days}d"
                    days < 365 -> "${days / 30}mo"
                    else -> "${days / 365}y"
                }
            } catch (e: Exception) {
                ""
            }
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean = oldItem == newItem
    }
} 