package com.example.agrosupport.presentation.reviewlist

data class ReviewCard (
    val id: Long,
    val farmerName: String,
    val comment: String,
    val rating: Int,
    val farmerLink: String
)