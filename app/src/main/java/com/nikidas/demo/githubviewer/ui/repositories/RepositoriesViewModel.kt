package com.nikidas.demo.githubviewer.ui.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.model.Repository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoriesViewModel(application: Application) : AndroidViewModel(application) {
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> = _repositories

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val api = GitHubApiService()

    fun loadRepositories() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val repos = withContext(Dispatchers.IO) {
                    api.getCurrentUserRepositories()
                }
                _repositories.value = repos ?: emptyList()
                _error.value = null
            } catch (e: Exception) {
                android.util.Log.e("RepositoriesViewModel", "Error loading repos", e)
                _error.value = e.message ?: "Load failed"
                _repositories.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
} 