package com.hasbi.jadwalku

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.utils.MD5Helper

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etNama: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        db = DatabaseHelper()

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInput(username, nama, email, password, confirmPassword)) {
                performRegister(username, nama, email, password)
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(
        username: String,
        nama: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            etUsername.error = "Username tidak boleh kosong"
            etUsername.requestFocus()
            return false
        }

        if (username.length < 4) {
            etUsername.error = "Username minimal 4 karakter"
            etUsername.requestFocus()
            return false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            etNama.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak sama"
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performRegister(username: String, nama: String, email: String, password: String) {
        showLoading(true)

        // Hash password dengan MD5
        val passwordMD5 = MD5Helper.md5(password)

        db.register(username, passwordMD5, nama, email, "mahasiswa") { success, message ->
            runOnUiThread {
                showLoading(false)

                if (success) {
                    Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
        etUsername.isEnabled = !show
        etNama.isEnabled = !show
        etEmail.isEnabled = !show
        etPassword.isEnabled = !show
        etConfirmPassword.isEnabled = !show
    }
}