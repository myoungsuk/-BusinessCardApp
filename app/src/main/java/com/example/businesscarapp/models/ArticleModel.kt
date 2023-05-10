package com.example.businesscarapp.models

data class ArticleModel(
    val noticeId: String,
    val title: String,
    val createdAt: Long,
    val content: String,
    val imageUrl: String
){

    constructor(): this("", "", 0, "", "")

}

