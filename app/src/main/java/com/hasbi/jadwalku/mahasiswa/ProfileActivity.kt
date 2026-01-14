package com.hasbi.jadwalku.mahasiswa

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hasbi.jadwalku.R
import com.hasbi.jadwalku.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profil Saya"

        sessionManager = SessionManager(this)

        tvUsername = findViewById(R.id.tvUsername)
        tvNama = findViewById(R.id.tvNama)
        tvEmail = findViewById(R.id.tvEmail)
        tvRole = findViewById(R.id.tvRole)

        loadProfile()
    }

    private fun loadProfile() {
        val userData = sessionManager.getUserData()

        tvUsername.text = userData["username"] ?: "-"
        tvNama.text = userData["nama"] ?: "-"
        tvEmail.text = userData["email"] ?: "-"

        // Capitalize role
        val role = userData["role"]?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        } ?: "-"
        tvRole.text = role
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}