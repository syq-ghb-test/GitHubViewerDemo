package com.nikidas.demo.githubviewer.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.repository.SearchRepository
import com.nikidas.demo.githubviewer.ui.common.UiState
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    val uiState = MutableLiveData<UiState<List<Repository>>>()

    fun search(query: String) {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val repos = repository.searchRepositories(query)
                uiState.value = UiState.Success(repos)
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "加载失败")
            }
        }
    }
}