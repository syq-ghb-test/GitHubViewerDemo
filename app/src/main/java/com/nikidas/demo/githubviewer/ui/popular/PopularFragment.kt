package com.nikidas.demo.githubviewer.ui.popular

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nikidas.demo.githubviewer.data.repository.PopularRepository
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.ui.common.ErrorLayout
import com.nikidas.demo.githubviewer.ui.common.UiState

class PopularFragment : Fragment() {
    private val viewModel: PopularViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val api = GitHubApiService()
                val repo = PopularRepository(api)
                @Suppress("UNCHECKED_CAST")
                return PopularViewModel(repo) as T
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val root = FrameLayout(context)

        // 置顶filter bar
        val filterLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }
        var currentTime = viewModel.currentTime
        var currentLanguage = viewModel.currentLanguage
        val btnTime = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
        btnTime.text = currentTime
        btnTime.setOnClickListener { v ->
            val popup = androidx.appcompat.widget.PopupMenu(context, v)
            popup.menu.add("Today")
            popup.menu.add("This week")
            popup.menu.add("This month")
            popup.setOnMenuItemClickListener {
                btnTime.text = it.title
                currentTime = it.title.toString()
                viewModel.setFilters(currentTime, currentLanguage)
                true
            }
            popup.show()
        }
        val btnLanguage = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
        btnLanguage.text = if (currentLanguage.isEmpty()) "Any" else currentLanguage
        btnLanguage.setOnClickListener { v ->
            val popup = androidx.appcompat.widget.PopupMenu(context, v)
            popup.menu.add("Any")
            popup.menu.add("C")
            popup.menu.add("C++")
            popup.menu.add("Java")
            popup.menu.add("JavaScript")
            popup.menu.add("Kotlin")
            popup.menu.add("Object-C")
            popup.menu.add("Python")
            popup.menu.add("TypeScript")
            popup.menu.add("Swift")
            popup.setOnMenuItemClickListener {
                btnLanguage.text = it.title
                currentLanguage = it.title.toString()
                viewModel.setFilters(currentTime, currentLanguage)
                true
            }
            popup.show()
        }
        
        filterLayout.addView(btnTime)
        filterLayout.addView(btnLanguage)

        // 列表
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PopularRepoAdapter()
        recyclerView.adapter = adapter

        // 垂直组合
        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        contentLayout.addView(filterLayout)
        contentLayout.addView(recyclerView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val errorLayout = ErrorLayout(context)
        errorLayout.visibility = View.GONE
        errorLayout.setOnRetryClickListener {
            viewModel.loadPopularRepos()
        }

        val progressBar = android.widget.ProgressBar(context)
        progressBar.visibility = View.GONE
        root.addView(contentLayout, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        root.addView(errorLayout, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        root.addView(progressBar, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        ))

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    errorLayout.visibility = View.GONE
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    adapter.submitList(state.data) {
                        recyclerView.scrollToPosition(0)
                    }
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                    errorLayout.setErrorText(state.message)
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.loadPopularRepos()
        return root
    }
}