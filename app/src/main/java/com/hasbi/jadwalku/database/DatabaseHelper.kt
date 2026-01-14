package com.hasbi.jadwalku.database

import android.util.Log
import com.hasbi.jadwalku.model.Jadwal
import com.hasbi.jadwalku.model.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class DatabaseHelper {

    private val BASE_URL = "https://appocalypse.my.id/jadwalku_api.php"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "DatabaseHelper"
    }

    // ==================== USER AUTHENTICATION ====================

    /**
     * Login User
     * @param username Username
     * @param passwordMD5 Password yang sudah di-MD5
     * @param callback Callback dengan User object jika berhasil, null jika gagal
     */
    fun login(username: String, passwordMD5: String, callback: (User?, String) -> Unit) {
        try {
            val url = "$BASE_URL?proc=login" +
                    "&username=${URLEncoder.encode(username, "UTF-8")}" +
                    "&password=$passwordMD5"

            Log.d(TAG, "Login URL: $url")

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Login error: ${e.message}")
                    callback(null, "Koneksi gagal: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        Log.d(TAG, "Login response: $responseBody")

                        if (responseBody.isNullOrEmpty()) {
                            callback(null, "Response kosong dari server")
                            return
                        }

                        val jsonResponse = JSONObject(responseBody)
                        val success = jsonResponse.optBoolean("success", false)
                        val message = jsonResponse.optString("message", "")

                        if (success) {
                            val data = jsonResponse.getJSONObject("data")
                            val user = User(
                                id = data.optString("id", ""),
                                username = data.optString("username", ""),
                                nama = data.optString("nama", ""),
                                email = data.optString("email", ""),
                                role = data.optString("role", "")
                            )
                            callback(user, message)
                        } else {
                            callback(null, message)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse error: ${e.message}")
                        callback(null, "Error parsing data: ${e.message}")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Login exception: ${e.message}")
            callback(null, "Error: ${e.message}")
        }
    }

    /**
     * Register User Baru
     * @param username Username
     * @param passwordMD5 Password yang sudah di-MD5
     * @param nama Nama lengkap
     * @param email Email
     * @param role Role (admin/mahasiswa)
     * @param callback Callback dengan status berhasil/gagal
     */
    fun register(
        username: String,
        passwordMD5: String,
        nama: String,
        email: String,
        role: String = "mahasiswa",
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val url = "$BASE_URL?proc=register" +
                    "&username=${URLEncoder.encode(username, "UTF-8")}" +
                    "&password=$passwordMD5" +
                    "&nama=${URLEncoder.encode(nama, "UTF-8")}" +
                    "&email=${URLEncoder.encode(email, "UTF-8")}" +
                    "&role=$role"

            Log.d(TAG, "Register URL: $url")

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Register error: ${e.message}")
                    callback(false, "Koneksi gagal: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        Log.d(TAG, "Register response: $responseBody")

                        if (responseBody.isNullOrEmpty()) {
                            callback(false, "Response kosong dari server")
                            return
                        }

                        val jsonResponse = JSONObject(responseBody)
                        val success = jsonResponse.optBoolean("success", false)
                        val message = jsonResponse.optString("message", "")

                        callback(success, message)
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse error: ${e.message}")
                        callback(false, "Error parsing data: ${e.message}")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Register exception: ${e.message}")
            callback(false, "Error: ${e.message}")
        }
    }

    // ==================== JADWAL OPERATIONS ====================

    /**
     * Get All Jadwal
     */
    fun getAllJadwal(callback: (List<Jadwal>) -> Unit) {
        val jadwalList = mutableListOf<Jadwal>()
        val url = "$BASE_URL?proc=getdata"

        Log.d(TAG, "Get all jadwal URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        try {
                            val jsonData = responseBody.string()
                            Log.d(TAG, "Response: $jsonData")

                            if (jsonData.isEmpty() || jsonData == "[]") {
                                callback(emptyList())
                                return
                            }

                            val jsonArray = JSONArray(jsonData)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val jadwal = Jadwal(
                                    id = jsonObject.optString("id", ""),
                                    mata_kuliah = jsonObject.optString("mata_kuliah", ""),
                                    dosen = jsonObject.optString("dosen", ""),
                                    hari = jsonObject.optString("hari", ""),
                                    jam = jsonObject.optString("jam", ""),
                                    ruangan = jsonObject.optString("ruangan", ""),
                                    sks = jsonObject.optString("sks", "0")
                                )
                                jadwalList.add(jadwal)
                            }

                            callback(jadwalList)
                        } catch (e: Exception) {
                            Log.e(TAG, "JSON error: ${e.message}")
                            callback(emptyList())
                        }
                    } ?: callback(emptyList())
                } else {
                    callback(emptyList())
                }
            }
        })
    }

    /**
     * Get Jadwal by ID
     */
    fun getJadwalById(id: String, callback: (Jadwal?) -> Unit) {
        val url = "$BASE_URL?proc=getdata&id=$id"
        Log.d(TAG, "Get by ID URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        try {
                            val jsonData = responseBody.string()
                            Log.d(TAG, "Response by ID: $jsonData")

                            val jsonArray = JSONArray(jsonData)

                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)
                                val jadwal = Jadwal(
                                    id = jsonObject.optString("id", ""),
                                    mata_kuliah = jsonObject.optString("mata_kuliah", ""),
                                    dosen = jsonObject.optString("dosen", ""),
                                    hari = jsonObject.optString("hari", ""),
                                    jam = jsonObject.optString("jam", ""),
                                    ruangan = jsonObject.optString("ruangan", ""),
                                    sks = jsonObject.optString("sks", "0")
                                )
                                callback(jadwal)
                            } else {
                                callback(null)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error: ${e.message}")
                            callback(null)
                        }
                    } ?: callback(null)
                } else {
                    callback(null)
                }
            }
        })
    }

    /**
     * Insert Jadwal (Admin Only)
     */
    fun insertJadwal(jadwal: Jadwal, callback: (Boolean, String) -> Unit) {
        try {
            val url = "$BASE_URL?proc=insert" +
                    "&mata_kuliah=${URLEncoder.encode(jadwal.mata_kuliah, "UTF-8")}" +
                    "&dosen=${URLEncoder.encode(jadwal.dosen, "UTF-8")}" +
                    "&hari=${URLEncoder.encode(jadwal.hari, "UTF-8")}" +
                    "&jam=${URLEncoder.encode(jadwal.jam, "UTF-8")}" +
                    "&ruangan=${URLEncoder.encode(jadwal.ruangan, "UTF-8")}" +
                    "&sks=${jadwal.sks}"

            Log.d(TAG, "Insert URL: $url")

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error: ${e.message}")
                    callback(false, "Koneksi gagal")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        Log.d(TAG, "Insert response: $responseBody")

                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val success = jsonResponse.optBoolean("success", false)
                        val message = jsonResponse.optString("message", "")

                        callback(success, message)
                    } catch (e: Exception) {
                        callback(response.isSuccessful, "")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            callback(false, "Error: ${e.message}")
        }
    }

    /**
     * Update Jadwal (Admin Only)
     */
    fun updateJadwal(jadwal: Jadwal, callback: (Boolean, String) -> Unit) {
        try {
            val url = "$BASE_URL?proc=update" +
                    "&id=${jadwal.id}" +
                    "&mata_kuliah=${URLEncoder.encode(jadwal.mata_kuliah, "UTF-8")}" +
                    "&dosen=${URLEncoder.encode(jadwal.dosen, "UTF-8")}" +
                    "&hari=${URLEncoder.encode(jadwal.hari, "UTF-8")}" +
                    "&jam=${URLEncoder.encode(jadwal.jam, "UTF-8")}" +
                    "&ruangan=${URLEncoder.encode(jadwal.ruangan, "UTF-8")}" +
                    "&sks=${jadwal.sks}"

            Log.d(TAG, "Update URL: $url")

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error: ${e.message}")
                    callback(false, "Koneksi gagal")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        Log.d(TAG, "Update response: $responseBody")

                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val success = jsonResponse.optBoolean("success", false)
                        val message = jsonResponse.optString("message", "")

                        callback(success, message)
                    } catch (e: Exception) {
                        callback(response.isSuccessful, "")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            callback(false, "Error: ${e.message}")
        }
    }

    /**
     * Delete Jadwal (Admin Only)
     */
    fun deleteJadwal(id: String, callback: (Boolean, String) -> Unit) {
        val url = "$BASE_URL?proc=delete&id=$id"
        Log.d(TAG, "Delete URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(false, "Koneksi gagal")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Delete response: $responseBody")

                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val success = jsonResponse.optBoolean("success", false)
                    val message = jsonResponse.optString("message", "")

                    callback(success, message)
                } catch (e: Exception) {
                    callback(response.isSuccessful, "")
                }
            }
        })
    }

    // ==================== FAVORITE OPERATIONS (Mahasiswa) ====================

    /**
     * Get Jadwal Favorit User
     */
    fun getFavorites(userId: String, callback: (List<Jadwal>) -> Unit) {
        val jadwalList = mutableListOf<Jadwal>()
        val url = "$BASE_URL?proc=get_favorites&user_id=$userId"

        Log.d(TAG, "Get favorites URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        try {
                            val jsonData = responseBody.string()
                            Log.d(TAG, "Favorites response: $jsonData")

                            if (jsonData.isEmpty() || jsonData == "[]") {
                                callback(emptyList())
                                return
                            }

                            val jsonArray = JSONArray(jsonData)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val jadwal = Jadwal(
                                    id = jsonObject.optString("id", ""),
                                    mata_kuliah = jsonObject.optString("mata_kuliah", ""),
                                    dosen = jsonObject.optString("dosen", ""),
                                    hari = jsonObject.optString("hari", ""),
                                    jam = jsonObject.optString("jam", ""),
                                    ruangan = jsonObject.optString("ruangan", ""),
                                    sks = jsonObject.optString("sks", "0")
                                )
                                jadwalList.add(jadwal)
                            }

                            callback(jadwalList)
                        } catch (e: Exception) {
                            Log.e(TAG, "JSON error: ${e.message}")
                            callback(emptyList())
                        }
                    } ?: callback(emptyList())
                } else {
                    callback(emptyList())
                }
            }
        })
    }

    /**
     * Check if Jadwal is Favorite
     */
    fun isFavorite(userId: String, jadwalId: String, callback: (Boolean) -> Unit) {
        val url = "$BASE_URL?proc=is_favorite&user_id=$userId&jadwal_id=$jadwalId"
        Log.d(TAG, "Is favorite URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Is favorite response: $responseBody")

                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val isFav = jsonResponse.optBoolean("is_favorite", false)

                    callback(isFav)
                } catch (e: Exception) {
                    callback(false)
                }
            }
        })
    }

    /**
     * Add to Favorite
     */
    fun addToFavorite(userId: String, jadwalId: String, callback: (Boolean, String) -> Unit) {
        val url = "$BASE_URL?proc=add_favorite&user_id=$userId&jadwal_id=$jadwalId"
        Log.d(TAG, "Add favorite URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(false, "Koneksi gagal")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Add favorite response: $responseBody")

                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val success = jsonResponse.optBoolean("success", false)
                    val message = jsonResponse.optString("message", "")

                    callback(success, message)
                } catch (e: Exception) {
                    callback(response.isSuccessful, "")
                }
            }
        })
    }

    /**
     * Remove from Favorite
     */
    fun removeFromFavorite(userId: String, jadwalId: String, callback: (Boolean, String) -> Unit) {
        val url = "$BASE_URL?proc=remove_favorite&user_id=$userId&jadwal_id=$jadwalId"
        Log.d(TAG, "Remove favorite URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.message}")
                callback(false, "Koneksi gagal")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Remove favorite response: $responseBody")

                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val success = jsonResponse.optBoolean("success", false)
                    val message = jsonResponse.optString("message", "")

                    callback(success, message)
                } catch (e: Exception) {
                    callback(response.isSuccessful, "")
                }
            }
        })
    }
}