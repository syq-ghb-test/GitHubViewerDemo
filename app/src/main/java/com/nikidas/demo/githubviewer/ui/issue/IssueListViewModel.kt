package com.nikidas.demo.githubviewer.ui.issue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.remote.Issue
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IssueListViewModel(application: Application) : AndroidViewModel(application) {
    private val _issues = MutableLiveData<List<Issue>>()
    val issues: LiveData<List<Issue>> = _issues

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val api = GitHubApiService()

    fun loadIssues(owner: String, repo: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val issues = withContext(Dispatchers.IO) {
                    api.getRepoIssues(owner, repo)
                }
                _issues.value = issues ?: emptyList()
                _error.value = null
            } catch (e: Exception) {
                android.util.Log.e("IssueListViewModel", "Error loading issues", e)
                _error.value = e.message ?: "Load failed"
                _issues.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
} 