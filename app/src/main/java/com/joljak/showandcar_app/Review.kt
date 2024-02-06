package com.joljak.showandcar_app

data class Review(
    val reviewId: String,
    val image: String,
    val title: String,
    val author: String,
    val ratingBar: Float,
    val postDate: String
)