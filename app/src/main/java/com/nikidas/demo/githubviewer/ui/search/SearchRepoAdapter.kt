package com.nikidas.demo.githubviewer.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.ui.common.RepositoryItemView
import com.nikidas.demo.githubviewer.ui.detail.RepoDetailFragment

class SearchRepoAdapter : ListAdapter<Repository, SearchRepoAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = RepositoryItemView(parent.context)
        itemView.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(itemView: RepositoryItemView) : RecyclerView.ViewHolder(itemView) {
        fun bind(repo: Repository) {
            (itemView as RepositoryItemView).bind(repo)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, com.nikidas.demo.githubviewer.ui.detail.DetailActivity::class.java).apply {
                    putExtra("repo_owner", repo.owner.login)
                    putExtra("repo_name", repo.name)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean = oldItem == newItem
    }
}
