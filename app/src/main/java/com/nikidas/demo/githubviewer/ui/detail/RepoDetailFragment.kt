package com.nikidas.demo.githubviewer.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.ui.common.ErrorLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.nikidas.demo.githubviewer.ui.issue.IssueListActivity

class RepoDetailFragment : Fragment() {
    private lateinit var tvRepoName: TextView
    private lateinit var tvRepoDesc: TextView
    private lateinit var tvRepoLink: TextView
    private lateinit var tvStars: TextView
    private lateinit var tvForks: TextView
    private lateinit var webView: WebView
    private lateinit var entryIssues: View
    private lateinit var entryReleases: View
    private lateinit var entryFiles: View
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: ErrorLayout
    private lateinit var contentScroll: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_repo_detail, container, false)
        tvRepoName = view.findViewById(R.id.tvRepoName)
        tvRepoDesc = view.findViewById(R.id.tvRepoDesc)
        tvRepoLink = view.findViewById(R.id.tvRepoLink)
        tvStars = view.findViewById(R.id.tvStars)
        tvForks = view.findViewById(R.id.tvForks)
        webView = view.findViewById(R.id.webView)
        entryIssues = view.findViewById(R.id.entryIssues)
        entryReleases = view.findViewById(R.id.entryReleases)
        entryFiles = view.findViewById(R.id.entryFiles)
        progressBar = view.findViewById(R.id.progressBar)
        errorLayout = view.findViewById(R.id.errorLayout)
        contentScroll = view.findViewById(R.id.contentScroll)

        setEntryClick(entryIssues, "Issues")
        setEntryClick(entryReleases, "Releases")
        setEntryClick(entryFiles, "Files")

        val filesColor = requireContext().getColor(android.R.color.primary_text_light)
        setEntryIconAndTitle(entryIssues, "📝", "Issues", filesColor)
        setEntryIconAndTitle(entryReleases, "🏷️", "Releases", filesColor)
        setEntryIconAndTitle(entryFiles, "📁", "Files", filesColor)
        entryFiles.findViewById<TextView>(R.id.tvTitle).setTextColor(filesColor)

        errorLayout.setOnRetryClickListener {
            val repoName = arguments?.getString("repo_name") ?: ""
            val owner = arguments?.getString("repo_owner") ?: ""
            loadData(owner, repoName)
        }

        val repoName = arguments?.getString("repo_name") ?: ""
        val owner = arguments?.getString("repo_owner") ?: ""
        loadData(owner, repoName)

        entryIssues.setOnClickListener {
            val intent = Intent(requireContext(), IssueListActivity::class.java).apply {
                putExtra("repo_owner", owner)
                putExtra("repo_name", repoName)
            }
            startActivity(intent)
        }

        return view
    }

    private fun setEntryClick(entry: View, name: String) {
        entry.setOnClickListener {
            if (name == "Files") {
                val repoName = arguments?.getString("repo_name") ?: return@setOnClickListener
                val owner = arguments?.getString("repo_owner") ?: return@setOnClickListener
                val intent = Intent(requireContext(), com.nikidas.demo.githubviewer.ui.file.FileListActivity::class.java)
                intent.putExtra("repo_name", repoName)
                intent.putExtra("repo_owner", owner)
                intent.putExtra("path", "") // 根目录
                startActivity(intent)
            } else {
                android.widget.Toast.makeText(requireContext(), "$name 功能暂不支持", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setEntryIconAndTitle(entry: View, emoji: String, title: String, color: Int? = null) {
        entry.findViewById<TextView>(R.id.ivIcon).text = emoji
        val tvTitle = entry.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = title
        color?.let { tvTitle.setTextColor(it) }
    }

    private fun loadData(owner: String, repoName: String) {
        progressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        contentScroll.visibility = View.GONE
        MainScope().launch {
            try {
                val api = GitHubApiService()
                val repo = withContext(Dispatchers.IO) {
                    api.getRepository(owner, repoName)
                }
                tvRepoName.text = repo.name
                tvRepoDesc.text = repo.description ?: ""
                tvRepoLink.text = repo.html_url
                tvStars.text = repo.stargazers_count.toString() + " stars"
                tvForks.text = repo.forks_count.toString() + " forks"
                setEntryCount(entryIssues, repo.open_issues_count)
                setEntryCount(entryReleases, repo.releases_count)
                entryFiles.findViewById<TextView>(R.id.tvCount).visibility = View.GONE
                val readme = withContext(Dispatchers.IO) {
                    api.getReadmeHtml(owner, repoName)
                }
                webView.loadDataWithBaseURL(null, readme, "text/html", "utf-8", null)
                progressBar.visibility = View.GONE
                errorLayout.visibility = View.GONE
                contentScroll.visibility = View.VISIBLE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                contentScroll.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                errorLayout.setErrorText(e.message ?: "加载失败")
            }
        }
    }

    private fun setEntryCount(entry: View, count: Int) {
        entry.findViewById<TextView>(R.id.tvCount).text = count.toString()
    }
}
