package com.hasbi.jadwalku

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hasbi.jadwalku.admin.AdminMainActivity
import com.hasbi.jadwalku.mahasiswa.MahasiswaMainActivity
import com.hasbi.jadwalku.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private val splashDelay: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        // Auto navigate setelah 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashDelay)
    }

    private fun navigateToNextScreen() {
        if (sessionManager.isLoggedIn()) {
            // Sudah login, langsung ke main activity sesuai role
            if (sessionManager.isAdmin()) {
                startActivity(Intent(this, AdminMainActivity::class.java))
            } else {
                startActivity(Intent(this, MahasiswaMainActivity::class.java))
            }
        } else {
            // Belum login, ke login screen
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}