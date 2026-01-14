package com.hasbi.jadwalku.model

data class Jadwal(
    val id: String,
    val mata_kuliah: String,
    val dosen: String,
    val hari: String,
    val jam: String,
    val ruangan: String,
    val sks: String
)