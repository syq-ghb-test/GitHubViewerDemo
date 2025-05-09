package com.nikidas.demo.githubviewer.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.ui.common.ErrorLayout
import com.nikidas.demo.githubviewer.ui.common.UiState

class DetailActivity : AppCompatActivity() {
    private lateinit var container: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: ErrorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        container = findViewById(R.id.container)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)

        if (savedInstanceState == null) {
            loadFragment()
        }

        errorLayout.setOnRetryClickListener {
            loadFragment()
        }
    }

    private fun loadFragment() {
        val fragment = RepoDetailFragment().apply {
            arguments = intent.extras
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun updateUiState(state: UiState<Unit>) {
        when (state) {
            is UiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                container.visibility = View.GONE
                errorLayout.visibility = View.GONE
            }

            is UiState.Success -> {
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
            }

            is UiState.Error -> {
                progressBar.visibility = View.GONE
                container.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                errorLayout.setErrorText(state.message)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 