package com.nikidas.demo.githubviewer.ui.issue

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.UnknownHostException

class CreateIssueActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etBody: EditText
    private lateinit var tvRepo: TextView
    private lateinit var fabSend: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private var repoOwner: String = ""
    private var repoName: String = ""
    private val api = GitHubApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_issue)

        supportActionBar?.title = "Create Issue"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etTitle = findViewById(R.id.etTitle)
        etBody = findViewById(R.id.etBody)
        tvRepo = findViewById(R.id.tvRepo)
        fabSend = findViewById(R.id.fabSend)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        val root = findViewById<View>(android.R.id.content) as View
        val params = android.widget.FrameLayout.LayoutParams(
            120, 120
        )
        params.gravity = android.view.Gravity.CENTER
        addContentView(progressBar, params)
        progressBar.visibility = View.GONE

        repoOwner = intent.getStringExtra("repo_owner") ?: ""
        repoName = intent.getStringExtra("repo_name") ?: ""
        tvRepo.text = "$repoOwner/$repoName"

        fabSend.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val body = etBody.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progressBar.visibility = View.VISIBLE
            fabSend.isEnabled = false
            MainScope().launch {
                try {
                    withContext(Dispatchers.IO) {
                        api.createIssue(repoOwner, repoName, title, if (body.isEmpty()) null else body)
                    }
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    progressBar.visibility = View.GONE
                    fabSend.isEnabled = true
                    val msg = when (e) {
                        is UnknownHostException, is ConnectException -> "Network is unavailable"
                        else -> e.message ?: "Create failed"
                    }
                    Toast.makeText(this@CreateIssueActivity, msg, Toast.LENGTH_LONG).show()                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 