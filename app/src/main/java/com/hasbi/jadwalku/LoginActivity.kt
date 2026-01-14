package com.hasbi.jadwalku

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.hasbi.jadwalku.admin.AdminMainActivity
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.mahasiswa.MahasiswaMainActivity
import com.hasbi.jadwalku.utils.MD5Helper
import com.hasbi.jadwalku.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var sessionManager: SessionManager
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        sessionManager = SessionManager(this)
        db = DatabaseHelper()

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                performLogin(username, password)
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            etUsername.error = "Username tidak boleh kosong"
            etUsername.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 4) {
            etPassword.error = "Password minimal 4 karakter"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(username: String, password: String) {
        showLoading(true)

        // Hash password dengan MD5
        val passwordMD5 = MD5Helper.md5(password)

        db.login(username, passwordMD5) { user, message ->
            runOnUiThread {
                showLoading(false)

                if (user != null) {
                    // Login berhasil, simpan session
                    sessionManager.createLoginSession(
                        userId = user.id,
                        username = user.username,
                        nama = user.nama,
                        email = user.email,
                        role = user.role
                    )

                    Toast.makeText(this, "Selamat datang, ${user.nama}!", Toast.LENGTH_SHORT).show()

                    // Navigate sesuai role
                    if (user.role == "admin") {
                        startActivity(Intent(this, AdminMainActivity::class.java))
                    } else {
                        startActivity(Intent(this, MahasiswaMainActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
        etUsername.isEnabled = !show
        etPassword.isEnabled = !show
    }
}