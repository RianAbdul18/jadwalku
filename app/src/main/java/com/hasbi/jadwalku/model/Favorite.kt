package com.hasbi.jadwalku.model

data class Favorite(
    val id: String,
    val user_id: String,
    val jadwal_id: String,
    val jadwal: Jadwal? = null // Optional, untuk join query
)