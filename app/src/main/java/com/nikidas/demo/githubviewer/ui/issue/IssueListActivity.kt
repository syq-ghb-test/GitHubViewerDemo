package com.nikidas.demo.githubviewer.ui.issue

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IssueListActivity : AppCompatActivity() {
    private val viewModel: IssueListViewModel by viewModels()
    private lateinit var adapter: IssueAdapter
    private var isOwner: Boolean = false
    private var repoOwner: String = ""
    private var repoName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_list)

        supportActionBar?.title = "Issues"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorLayout = findViewById<ErrorLayout>(R.id.errorLayout)
        val tvEmpty = findViewById<android.widget.TextView>(R.id.tvEmpty)
        val fabAddIssue = findViewById<FloatingActionButton>(R.id.fabAddIssue)

        adapter = IssueAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        repoOwner = intent.getStringExtra("repo_owner") ?: ""
        repoName = intent.getStringExtra("repo_name") ?: ""

        // 判断是否为自己仓库
        val user = LoginManager.getUser(this)
        isOwner = user?.username == repoOwner
        if (isOwner) {
            fabAddIssue.visibility = View.VISIBLE
            fabAddIssue.setOnClickListener {
                val intent = Intent(this, CreateIssueActivity::class.java)
                intent.putExtra("repo_owner", repoOwner)
                intent.putExtra("repo_name", repoName)
                startActivityForResult(intent, 1001)
            }
        } else {
            fabAddIssue.visibility = View.GONE
        }
        invalidateOptionsMenu()

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadIssues(repoOwner, repoName)
        }

        viewModel.issues.observe(this) { issues ->
            adapter.submitList(issues)
            swipeRefreshLayout.isRefreshing = false
            tvEmpty.visibility = if (issues.isEmpty()) View.VISIBLE else View.GONE
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

        viewModel.loadIssues(repoOwner, repoName)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 不再添加 add issue 菜单
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.loadIssues(repoOwner, repoName)
            }, 1500)
        }
    }
} 