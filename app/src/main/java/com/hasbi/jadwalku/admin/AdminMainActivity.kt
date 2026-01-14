package com.hasbi.jadwalku.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hasbi.jadwalku.LoginActivity
import com.hasbi.jadwalku.R
import com.hasbi.jadwalku.adapter.JadwalAdminAdapter
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.model.Jadwal
import com.hasbi.jadwalku.utils.SessionManager

class AdminMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jadwalAdapter: JadwalAdminAdapter
    private lateinit var db: DatabaseHelper
    private lateinit var btnTambah: FloatingActionButton
    private lateinit var tvWelcome: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        sessionManager = SessionManager(this)

        // Set title
        supportActionBar?.title = "Admin - Kelola Jadwal"

        recyclerView = findViewById(R.id.recyclerView)
        btnTambah = findViewById(R.id.btnTambah)
        tvWelcome = findViewById(R.id.tvWelcome)

        // Set welcome message
        tvWelcome.text = "Selamat datang, ${sessionManager.getNama()}!"

        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DatabaseHelper()

        val emptyList = mutableListOf<Jadwal>()
        jadwalAdapter = JadwalAdminAdapter(emptyList, this) {
            loadJadwal()
        }
        recyclerView.adapter = jadwalAdapter

        loadJadwal()

        btnTambah.setOnClickListener {
            startActivity(Intent(this, TambahJadwalActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadJadwal()
    }

    private fun loadJadwal() {
        db.getAllJadwal { jadwalList ->
            runOnUiThread {
                if (jadwalList.isNotEmpty()) {
                    jadwalAdapter.refreshData(jadwalList)
                } else {
                    jadwalAdapter.refreshData(emptyList())
                    Toast.makeText(this, "Belum ada data jadwal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            R.id.action_refresh -> {
                loadJadwal()
                Toast.makeText(this, "Data di-refresh", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                sessionManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}