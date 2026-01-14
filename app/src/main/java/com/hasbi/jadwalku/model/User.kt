package com.hasbi.jadwalku.model

data class User(
    val id: String,
    val username: String,
    val nama: String,
    val email: String,
    val role: String // "admin" atau "mahasiswa"
)