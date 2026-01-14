package com.hasbi.jadwalku.mahasiswa

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
import com.google.android.material.tabs.TabLayout
import com.hasbi.jadwalku.LoginActivity
import com.hasbi.jadwalku.R
import com.hasbi.jadwalku.adapter.JadwalMahasiswaAdapter
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.model.Jadwal
import com.hasbi.jadwalku.utils.SessionManager

class MahasiswaMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jadwalAdapter: JadwalMahasiswaAdapter
    private lateinit var db: DatabaseHelper
    private lateinit var tvWelcome: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mahasiswa_main)

        sessionManager = SessionManager(this)

        // Set title
        supportActionBar?.title = "Jadwal Kuliah"

        recyclerView = findViewById(R.id.recyclerView)
        tvWelcome = findViewById(R.id.tvWelcome)
        tabLayout = findViewById(R.id.tabLayout)

        // Set welcome message
        tvWelcome.text = "Halo, ${sessionManager.getNama()}!"

        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DatabaseHelper()

        val emptyList = mutableListOf<Jadwal>()
        jadwalAdapter = JadwalMahasiswaAdapter(
            emptyList,
            this,
            sessionManager.getUserId()
        ) {
            // Callback ketika favorite berubah
            loadCurrentTab()
        }
        recyclerView.adapter = jadwalAdapter

        // Setup TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Semua Jadwal"))
        tabLayout.addTab(tabLayout.newTab().setText("Favorit"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadAllJadwal()
                    1 -> loadFavorites()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load data pertama kali
        loadAllJadwal()
    }

    override fun onResume() {
        super.onResume()
        loadCurrentTab()
    }

    private fun loadCurrentTab() {
        when (tabLayout.selectedTabPosition) {
            0 -> loadAllJadwal()
            1 -> loadFavorites()
        }
    }

    private fun loadAllJadwal() {
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

    private fun loadFavorites() {
        db.getFavorites(sessionManager.getUserId()) { jadwalList ->
            runOnUiThread {
                if (jadwalList.isNotEmpty()) {
                    jadwalAdapter.refreshData(jadwalList)
                } else {
                    jadwalAdapter.refreshData(emptyList())
                    Toast.makeText(this, "Belum ada jadwal favorit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_mahasiswa, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                loadCurrentTab()
                Toast.makeText(this, "Data di-refresh", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
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