package com.nikidas.demo.githubviewer.data.repository

import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(private val api: GitHubApiService) {
    suspend fun searchRepositories(query: String): List<Repository> {
        return withContext(Dispatchers.IO) {
            api.searchRepositoriesSync(query)
        }
    }
} 