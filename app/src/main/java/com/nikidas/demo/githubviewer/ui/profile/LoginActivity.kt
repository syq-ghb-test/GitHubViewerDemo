package com.nikidas.demo.githubviewer.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.util.Constants
import com.nikidas.demo.githubviewer.util.LoginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private val githubApi = GitHubApiService()

    companion object {
        private const val TAG = "LoginActivity"
        fun startForResult(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.apply {
            title = "Login"
            setDisplayHomeAsUpEnabled(true)
        }

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        setupWebView()
        loadGitHubLogin()
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                Log.d(TAG, "Loading page: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                Log.d(TAG, "Page loaded: $url")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d(TAG, "Intercepting URL: $url")
                if (url.startsWith(Constants.REDIRECT_URI)) {
                    handleCallback(url)
                    return true
                }
                return false
            }
        }
    }

    private fun loadGitHubLogin() {
        val authUrl = "${Constants.GITHUB_AUTH_URL}?client_id=${Constants.CLIENT_ID}&redirect_uri=${Constants.REDIRECT_URI}&scope=repo"
        Log.d(TAG, "Loading auth URL: $authUrl")
        webView.loadUrl(authUrl)
    }

    private fun handleCallback(url: String) {
        Log.d(TAG, "Handling callback URL: $url")
        val code = url.substringAfter("code=")
        if (code.isNotEmpty()) {
            Log.d(TAG, "Got authorization code: $code")
            fetchUserInfo(code)
        } else {
            Log.e(TAG, "No authorization code in URL")
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun fetchUserInfo(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching access token...")
                val token = githubApi.getAccessToken(code)
                if (token != null) {
                    Log.d(TAG, "Got access token, fetching user info... token="+ token)
                    val sp = getSharedPreferences("login_prefs", MODE_PRIVATE)
                    sp.edit().putString("access_token", token).apply()
                    val user = githubApi.getCurrentUser()
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            Log.d(TAG, "User info retrieved successfully: ${user.login}")
                            LoginManager.saveUser(this@LoginActivity, user.login, user.avatar_url)

                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Log.e(TAG, "Failed to get user info")
                            Toast.makeText(this@LoginActivity, "Failed to get user info", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to get access token")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Failed to get access token", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during login process", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error during login: ${e.message}", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}