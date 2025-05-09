package com.nikidas.demo.githubviewer.data.model

data class Repository(
    val id: Long,
    val name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?,
    val owner: Owner
)

data class Owner(
    val login: String,
    val avatar_url: String
) 