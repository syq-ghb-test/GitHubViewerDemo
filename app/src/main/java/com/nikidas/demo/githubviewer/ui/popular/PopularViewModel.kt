package com.nikidas.demo.githubviewer.ui.popular

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikidas.demo.githubviewer.data.model.Repository
import com.nikidas.demo.githubviewer.data.repository.PopularRepository
import com.nikidas.demo.githubviewer.ui.common.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PopularViewModel(private val repository: PopularRepository) : ViewModel() {
    val uiState = MutableLiveData<UiState<List<Repository>>>()
    private var hasLoaded = false
    private var timeFilter: String = "This week"
    private var languageFilter: String = "Any"

    val currentTime: String get() = timeFilter
    val currentLanguage: String get() = languageFilter

    fun setFilters(time: String, language: String) {
        timeFilter = time
        languageFilter = language
        hasLoaded = false
        loadPopularRepos()
    }

    fun loadPopularRepos() {
        if (hasLoaded) return
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val query = buildQuery()
                val repos = repository.getPopularRepositories(query)
                uiState.value = UiState.Success<List<Repository>>(repos)
                hasLoaded = true
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message ?: "加载失败")
            }
        }
    }

    private fun buildQuery(): String {
        val time = when (timeFilter) {
            "Today" -> "created:>${getDateNDaysAgo(1)}"
            "This week" -> "created:>${getDateNDaysAgo(7)}"
            "This month" -> "created:>${getDateNDaysAgo(30)}"
            else -> ""
        }
        val lang = if (languageFilter == "Any") "" else " language:$languageFilter"
        return "$time$lang"
    }

    private fun getDateNDaysAgo(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -days)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }
}