package com.nikidas.demo.githubviewer.ui.file

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileListAdapter
    private var owner: String = ""
    private var repo: String = ""
    private var path: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)
        recyclerView = findViewById(R.id.recyclerView)

        owner = intent.getStringExtra("repo_owner") ?: ""
        repo = intent.getStringExtra("repo_name") ?: ""
        path = intent.getStringExtra("path") ?: ""
        supportActionBar?.title = if (path.isEmpty()) repo else path.substringAfterLast('/')
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = FileListAdapter { item ->
            if (item.type == "dir") {
                // 进入下一级
                val intent = Intent(this, FileListActivity::class.java)
                intent.putExtra("repo_owner", owner)
                intent.putExtra("repo_name", repo)
                intent.putExtra("path", item.path)
                startActivity(intent)
            } else {
                Toast.makeText(this, "暂不支持文件预览", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        loadFiles()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadFiles() {
        MainScope().launch {
            try {
                val api = GitHubApiService()
                val files = withContext(Dispatchers.IO) {
                    api.getRepoFiles(owner, repo, path)
                }
                adapter.submitList(files)
            } catch (e: Exception) {
                Toast.makeText(this@FileListActivity, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 