package com.nikidas.demo.githubviewer.data.remote

import com.nikidas.demo.githubviewer.MyApplication
import android.util.Log
import com.google.gson.Gson
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.model.Owner
import com.nikidas.demo.githubviewer.util.Constants
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import com.nikidas.demo.githubviewer.ui.file.RepoFileItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GitHubApiService(
    private val tokenProvider: () -> String? = {
        MyApplication.getInstance()
            .getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE)
            .getString("access_token", null)
    }
) {
    private val context = MyApplication.getInstance()
    private val client = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor(tokenProvider))
        .build()
    private val gson = Gson()

    companion object {
        private const val TAG = "GitHubApiService"
    }

    // 认证相关
    suspend fun getAccessToken(code: String): String? {
        val formBody = FormBody.Builder()
            .add("client_id", Constants.CLIENT_ID)
            .add("client_secret", Constants.CLIENT_SECRET)
            .add("code", code)
            .build()

        val request = Request.Builder()
            .url(Constants.GITHUB_TOKEN_URL)
            .post(formBody)
            .header("Accept", "application/json")
            .build()

        return try {
            Log.d(TAG, "Requesting access token with code: $code")
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d(TAG, "Token response: $responseBody")

            if (response.isSuccessful) {
                val tokenResponse = gson.fromJson(responseBody, TokenResponse::class.java)
                tokenResponse.access_token
            } else {
                Log.e(TAG, "Failed to get access token. Response code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting access token", e)
            null
        }
    }

    suspend fun getCurrentUser(): User? {
        val url = "${Constants.GITHUB_API_BASE_URL}user"
        val request = Request.Builder().url(url).build()
        return try {
            Log.d(TAG, "Requesting user info")
            val response = client.newCall(request).execute()
            Log.d(TAG, "getCurrentUser token=${tokenProvider()}")

            val responseBody = response.body?.string()
            Log.d(TAG, "User info response: $responseBody")

            if (response.isSuccessful) {
                gson.fromJson(responseBody, User::class.java)
            } else {
                Log.e(TAG, "Failed to get user info. Response code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user info", e)
            null
        }
    }

    // 仓库相关
    fun searchRepositoriesSync(query: String, sort: String = "stars"): List<Repository> {
        val url = "${Constants.GITHUB_API_BASE_URL}search/repositories?q=$query&sort=$sort"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("网络请求失败: ${response.code}")
            val body = response.body?.string() ?: throw Exception("响应体为空")
            val searchResponse = gson.fromJson(body, SearchResponse::class.java)
            return searchResponse.items
        }
    }

    fun getRepository(owner: String, repo: String): RepositoryDetail {
        val url = "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: throw Exception("响应体为空")
            Log.d(TAG, "getRepository: code=${response.code}, body=$body")
            if (!response.isSuccessful) throw Exception("获取仓库详情失败: ${response.code}, body=$body")
            return gson.fromJson(body, RepositoryDetail::class.java)
        }
    }

    suspend fun getUserRepositories(username: String): List<Repository>? {
        val url = "${Constants.GITHUB_API_BASE_URL}users/$username/repos"
        val request = Request.Builder().url(url).build()
        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                gson.fromJson(responseBody, Array<Repository>::class.java).toList()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCurrentUserRepositories(): List<Repository>? {
        val url = "${Constants.GITHUB_API_BASE_URL}user/repos"
        val request = Request.Builder().url(url).build()
        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                gson.fromJson(responseBody, Array<Repository>::class.java).toList()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Issue 相关
    suspend fun getRepoIssues(owner: String, repo: String): List<Issue> {
        val url = "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo/issues"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && responseBody != null) {
            return gson.fromJson(responseBody, Array<Issue>::class.java).toList()
        } else {
            throw Exception("Network error: code=${response.code}, body=$responseBody")
        }
    }

    // 文件相关
    fun getReadmeHtml(owner: String, repo: String): String {
        val url = "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo/readme"
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3.html")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("获取README失败: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    suspend fun getRepoFiles(owner: String, repo: String, path: String): List<RepoFileItem> {
        val url = if (path.isEmpty()) {
            "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo/contents"
        } else {
            "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo/contents/$path"
        }
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("获取文件列表失败: ${response.code}")
            val body = response.body?.string() ?: throw Exception("响应体为空")
            val arr = com.google.gson.JsonParser.parseString(body).asJsonArray
            return arr.map {
                val obj = it.asJsonObject
                RepoFileItem(
                    name = obj["name"].asString,
                    path = obj["path"].asString,
                    type = obj["type"].asString // "file" or "dir"
                )
            }
        }
    }

    fun getRawFileUrl(owner: String, repo: String, path: String): String {
        return "https://raw.githubusercontent.com/$owner/$repo/master/$path"
    }

    fun getFileTextContent(owner: String, repo: String, path: String): String {
        val url = getRawFileUrl(owner, repo, path)
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("获取文件内容失败: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    // 创建 Issue
    suspend fun createIssue(owner: String, repo: String, title: String, body: String?): Issue {
        val url = "${Constants.GITHUB_API_BASE_URL}repos/$owner/$repo/issues"
        val json = if (body.isNullOrEmpty()) {
            "{\"title\": \"$title\"}"
        } else {
            "{\"title\": \"$title\", \"body\": \"$body\"}"
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        Log.d(TAG, "createIssue url=$url")
        Log.d(TAG, "createIssue token=${tokenProvider()}")
        Log.d(TAG, "createIssue json=$json")
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        Log.d(TAG, "createIssue response code=${response.code}, body=$responseBody")
        if (response.isSuccessful && responseBody != null) {
            return gson.fromJson(responseBody, Issue::class.java)
        } else {
            if (response.code == 404) {
                throw Exception("404：仓库不存在或无权限，请检查仓库名、owner、token 权限")
            }
            throw Exception("创建 Issue 失败: code=${response.code}, body=$responseBody")
        }
    }
}

// 数据类定义
data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String
)

data class User(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val name: String?,
    val email: String?
)

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val created_at: String,
    val comments: Int
)

data class SearchResponse(val items: List<Repository>)

data class RepositoryDetail(
    val name: String,
    val description: String?,
    val stargazers_count: Int,
    val forks_count: Int,
    val html_url: String,
    val open_issues_count: Int,
    val releases_count: Int
) 