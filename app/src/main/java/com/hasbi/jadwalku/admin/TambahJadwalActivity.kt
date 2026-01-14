package com.hasbi.jadwalku.admin

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.hasbi.jadwalku.R
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.model.Jadwal

class TambahJadwalActivity : AppCompatActivity() {

    private lateinit var etMataKuliah: TextInputEditText
    private lateinit var etDosen: TextInputEditText
    private lateinit var spinnerHari: Spinner
    private lateinit var etJam: TextInputEditText
    private lateinit var etRuangan: TextInputEditText
    private lateinit var etSks: TextInputEditText
    private lateinit var btnSimpan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_jadwal)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tambah Jadwal"

        etMataKuliah = findViewById(R.id.etMataKuliah)
        etDosen = findViewById(R.id.etDosen)
        spinnerHari = findViewById(R.id.spinnerHari)
        etJam = findViewById(R.id.etJam)
        etRuangan = findViewById(R.id.etRuangan)
        etSks = findViewById(R.id.etSks)
        btnSimpan = findViewById(R.id.btnSimpan)
        progressBar = findViewById(R.id.progressBar)

        db = DatabaseHelper()

        // Setup Spinner Hari
        val hariList = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hariList)
        spinnerHari.adapter = adapter

        btnSimpan.setOnClickListener {
            simpanJadwal()
        }
    }

    private fun simpanJadwal() {
        val mataKuliah = etMataKuliah.text.toString().trim()
        val dosen = etDosen.text.toString().trim()
        val hari = spinnerHari.selectedItem.toString()
        val jam = etJam.text.toString().trim()
        val ruangan = etRuangan.text.toString().trim()
        val sks = etSks.text.toString().trim()

        if (!validateInput(mataKuliah, dosen, jam, sks)) {
            return
        }

        showLoading(true)

        val jadwal = Jadwal(
            id = "",
            mata_kuliah = mataKuliah,
            dosen = dosen,
            hari = hari,
            jam = jam,
            ruangan = ruangan,
            sks = sks
        )

        db.insertJadwal(jadwal) { success, message ->
            runOnUiThread {
                showLoading(false)

                if (success) {
                    Toast.makeText(this, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInput(mataKuliah: String, dosen: String, jam: String, sks: String): Boolean {
        if (mataKuliah.isEmpty()) {
            etMataKuliah.error = "Mata kuliah harus diisi"
            etMataKuliah.requestFocus()
            return false
        }

        if (dosen.isEmpty()) {
            etDosen.error = "Nama dosen harus diisi"
            etDosen.requestFocus()
            return false
        }

        if (jam.isEmpty()) {
            etJam.error = "Jam harus diisi"
            etJam.requestFocus()
            return false
        }

        if (sks.isEmpty()) {
            etSks.error = "SKS harus diisi"
            etSks.requestFocus()
            return false
        }

        return true
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSimpan.isEnabled = !show
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}