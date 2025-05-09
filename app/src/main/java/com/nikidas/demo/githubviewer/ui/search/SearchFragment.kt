package com.nikidas.demo.githubviewer.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikidas.demo.githubviewer.data.remote.GitHubApiService
import com.nikidas.demo.githubviewer.data.repository.SearchRepository
import com.nikidas.demo.githubviewer.ui.common.ErrorLayout
import com.nikidas.demo.githubviewer.ui.common.UiState

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val api = GitHubApiService()
                val repo = SearchRepository(api)
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(repo) as T
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val root = FrameLayout(context)

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        val editText = EditText(context).apply {
            hint = "Search Repository"
            setSingleLine(true)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1f
            )
        }
        val adapter = SearchRepoAdapter()
        recyclerView.adapter = adapter
        contentLayout.addView(editText)
        contentLayout.addView(recyclerView)

        val errorLayout = ErrorLayout(context)
        errorLayout.visibility = View.GONE
        errorLayout.setOnRetryClickListener {
            viewModel.search(editText.text.toString())
        }

        root.addView(contentLayout, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        root.addView(errorLayout, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER))

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    recyclerView.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    recyclerView.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                    errorLayout.setErrorText(state.message)
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    recyclerView.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                }
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }
}