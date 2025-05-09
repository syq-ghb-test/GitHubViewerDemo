package com.nikidas.demo.githubviewer.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.data.model.Repository

class RepositoryItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val ivAvatar: ImageView
    private val tvOwnerRepo: TextView
    private val tvDescription: TextView
    private val tvStars: TextView
    private val tvLanguage: TextView
    private val tvContributors: TextView

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.item_repository, this, true)
        ivAvatar = findViewById(R.id.iv_avatar)
        tvOwnerRepo = findViewById(R.id.tv_owner_repo)
        tvDescription = findViewById(R.id.tv_description)
        tvStars = findViewById(R.id.tv_stars)
        tvLanguage = findViewById(R.id.tv_language)
        tvContributors = findViewById(R.id.tv_contributors)
    }

    fun bind(repo: Repository) {
        Glide.with(ivAvatar).load(repo.owner.avatar_url).into(ivAvatar)
        tvOwnerRepo.text = "${repo.owner.login} / ${repo.name}"
        tvDescription.text = repo.description ?: ""
        tvStars.text = "⭐ ${formatStar(repo.stargazers_count)}"
        tvLanguage.text = if (!repo.language.isNullOrBlank()) "• ${repo.language}" else ""
        tvContributors.text = "" // 贡献者数后续可补充
    }

    private fun formatStar(star: Int): String {
        return if (star >= 1000) String.format("%.1fk", star / 1000.0) else star.toString()
    }
} 