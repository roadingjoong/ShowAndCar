package com.joljak.showandcar_app

data class Comment (
    val commentAuthor: String,
    val commentContent: String,
    val commentLikes: Int,
    val commentAnswer: Int,
    val commentPostDate: String,
    val userId : String
)