package com.nikidas.demo.githubviewer.ui.file

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class FilePreviewActivity : AppCompatActivity() {
    private lateinit var textContent: TextView
    private lateinit var imageContent: ImageView
    private lateinit var scrollView: ScrollView
    private var owner: String = ""
    private var repo: String = ""
    private var path: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_preview)
        textContent = findViewById(R.id.textContent)
        imageContent = findViewById(R.id.imageContent)
        scrollView = findViewById(R.id.scrollView)

        owner = intent.getStringExtra("repo_owner") ?: ""
        repo = intent.getStringExtra("repo_name") ?: ""
        path = intent.getStringExtra("path") ?: ""
        name = intent.getStringExtra("name") ?: ""
        supportActionBar?.title = name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val lower = name.lowercase()
        if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif") || lower.endsWith(".webp")) {
            showImage()
        } else if (lower.endsWith(".md") || lower.endsWith(".txt") || lower.endsWith(".java") || lower.endsWith(".kt") || lower.endsWith(".json") || lower.endsWith(".xml") || lower.endsWith(".yaml") || lower.endsWith(".yml") || lower.endsWith(".py") || lower.endsWith(".c") || lower.endsWith(".cpp") || lower.endsWith(".h") || lower.endsWith(".js") || lower.endsWith(".ts")) {
            showText()
        } else {
            Toast.makeText(this, "暂不支持预览该类型文件", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showText() {
        scrollView.visibility = View.VISIBLE
        imageContent.visibility = View.GONE
        MainScope().launch {
            try {
                val api = GitHubApiService()
                val content = withContext(Dispatchers.IO) {
                    api.getFileTextContent(owner, repo, path)
                }
                textContent.text = content
            } catch (e: Exception) {
                textContent.text = "加载失败: ${e.message}"
            }
        }
    }

    private fun showImage() {
        scrollView.visibility = View.GONE
        imageContent.visibility = View.VISIBLE
        MainScope().launch {
            try {
                val api = GitHubApiService()
                val url = withContext(Dispatchers.IO) {
                    api.getRawFileUrl(owner, repo, path)
                }
                val bitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeStream(URL(url).openStream())
                }
                imageContent.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this@FilePreviewActivity, "图片加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
} 