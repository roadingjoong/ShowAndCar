package com.joljak.showandcar_app

data class DeleteComment (
    val commentId : String,
    val commentAuthor: String,
    val commentContent: String,
    val commentLikes: Int,
    val commentAnswer: Int,
    val commentPostDate: String,
    var isSelected: Boolean = false
)