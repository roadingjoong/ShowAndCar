package com.joljak.showandcar_app

data class DeletePost (
    val postId: String,
    val image: String,
    val title: String,
    val comments: String,
    val likes: Int,
    val author: String,
    val postDate: String,
    var isSelected: Boolean = false
)