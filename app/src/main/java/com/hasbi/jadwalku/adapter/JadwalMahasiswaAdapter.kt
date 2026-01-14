package com.hasbi.jadwalku.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hasbi.jadwalku.R
import com.hasbi.jadwalku.database.DatabaseHelper
import com.hasbi.jadwalku.model.Jadwal

class JadwalMahasiswaAdapter(
    private var jadwalList: MutableList<Jadwal>,
    private val context: Context,
    private val userId: String,
    private val onFavoriteChanged: () -> Unit
) : RecyclerView.Adapter<JadwalMahasiswaAdapter.JadwalViewHolder>() {

    private val db = DatabaseHelper()
    private val favoriteStatus = mutableMapOf<String, Boolean>()

    class JadwalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMataKuliah: TextView = view.findViewById(R.id.tvMataKuliah)
        val tvDosen: TextView = view.findViewById(R.id.tvDosen)
        val tvHariJam: TextView = view.findViewById(R.id.tvHariJam)
        val tvRuangan: TextView = view.findViewById(R.id.tvRuangan)
        val tvSks: TextView = view.findViewById(R.id.tvSks)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal_mahasiswa, parent, false)
        return JadwalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val jadwal = jadwalList[position]

        holder.tvMataKuliah.text = jadwal.mata_kuliah
        holder.tvDosen.text = "Dosen: ${jadwal.dosen}"
        holder.tvHariJam.text = "${jadwal.hari}, ${jadwal.jam}"
        holder.tvRuangan.text = "Ruang: ${jadwal.ruangan}"
        holder.tvSks.text = "${jadwal.sks} SKS"

        // Check favorite status
        checkFavoriteStatus(jadwal.id) { isFavorite ->
            (context as? Activity)?.runOnUiThread {
                favoriteStatus[jadwal.id] = isFavorite
                updateFavoriteIcon(holder.btnFavorite, isFavorite)
            }
        }

        // Tombol Favorite
        holder.btnFavorite.setOnClickListener {
            val isFavorite = favoriteStatus[jadwal.id] ?: false
            toggleFavorite(jadwal.id, isFavorite, holder.btnFavorite)
        }
    }

    override fun getItemCount(): Int = jadwalList.size

    private fun checkFavoriteStatus(jadwalId: String, callback: (Boolean) -> Unit) {
        db.isFavorite(userId, jadwalId) { isFavorite ->
            callback(isFavorite)
        }
    }

    private fun toggleFavorite(jadwalId: String, currentStatus: Boolean, button: ImageButton) {
        if (currentStatus) {
            // Remove from favorite
            db.removeFromFavorite(userId, jadwalId) { success, message ->
                (context as? Activity)?.runOnUiThread {
                    if (success) {
                        favoriteStatus[jadwalId] = false
                        updateFavoriteIcon(button, false)
                        Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                        onFavoriteChanged()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Add to favorite
            db.addToFavorite(userId, jadwalId) { success, message ->
                (context as? Activity)?.runOnUiThread {
                    if (success) {
                        favoriteStatus[jadwalId] = true
                        updateFavoriteIcon(button, true)
                        Toast.makeText(context, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                        onFavoriteChanged()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite_filled)
            button.setColorFilter(ContextCompat.getColor(context, R.color.favorite_color))
        } else {
            button.setImageResource(R.drawable.ic_favorite_border)
            button.setColorFilter(ContextCompat.getColor(context, R.color.gray))
        }
    }

    fun refreshData(newData: List<Jadwal>) {
        jadwalList.clear()
        jadwalList.addAll(newData)
        favoriteStatus.clear()
        notifyDataSetChanged()
    }
}