package com.hasbi.jadwalku.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "JadwalkuSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAMA = "nama"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
    }

    /**
     * Simpan session login user
     */
    fun createLoginSession(userId: String, username: String, nama: String, email: String, role: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_NAMA, nama)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Get User ID
     */
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    /**
     * Get Username
     */
    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    /**
     * Get Nama Lengkap
     */
    fun getNama(): String {
        return prefs.getString(KEY_NAMA, "") ?: ""
    }

    /**
     * Get Email
     */
    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    /**
     * Get User Role
     */
    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "") ?: ""
    }

    /**
     * Check if user is Admin
     */
    fun isAdmin(): Boolean {
        return getRole() == "admin"
    }

    /**
     * Check if user is Mahasiswa
     */
    fun isMahasiswa(): Boolean {
        return getRole() == "mahasiswa"
    }

    /**
     * Get all user data as map
     */
    fun getUserData(): Map<String, String> {
        return mapOf(
            "id" to getUserId(),
            "username" to getUsername(),
            "nama" to getNama(),
            "email" to getEmail(),
            "role" to getRole()
        )
    }

    /**
     * Logout - hapus semua session
     */
    fun logout() {
        editor.clear()
        editor.apply()
    }

    /**
     * Update nama user
     */
    fun updateNama(nama: String) {
        editor.putString(KEY_NAMA, nama)
        editor.apply()
    }

    /**
     * Update email user
     */
    fun updateEmail(email: String) {
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }
}