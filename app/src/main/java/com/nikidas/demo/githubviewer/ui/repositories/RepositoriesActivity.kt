package com.nikidas.demo.githubviewer.ui.repositories

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.ui.common.ErrorLayout
import com.nikidas.demo.githubviewer.util.LoginManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.ProgressBar

class RepositoriesActivity : AppCompatActivity() {
    private val viewModel: RepositoriesViewModel by viewModels()
    private lateinit var adapter: RepositoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repositories)

        supportActionBar?.title = "Repositories"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorLayout = findViewById<ErrorLayout>(R.id.errorLayout)

        adapter = RepositoriesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRepositories()
        }

        viewModel.repositories.observe(this) { repos ->
            adapter.submitList(repos)
            swipeRefreshLayout.isRefreshing = false
        }

        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                errorLayout.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                errorLayout.visibility = View.VISIBLE
                errorLayout.setErrorText(error)
            } else {
                errorLayout.visibility = View.GONE
            }
        }

        viewModel.loadRepositories()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 