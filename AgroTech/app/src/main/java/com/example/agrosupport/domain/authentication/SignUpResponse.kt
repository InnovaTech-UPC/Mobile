package com.example.agrosupport.domain.authentication

data class SignUpResponse(
    val id : Long,
    val username : String,
    val roles : List<String>
)
