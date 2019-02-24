package id.droidindonesia.pencaribeasiswa.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lomba(
    val id: Int,
    val kategori: String,
    val created_at: String,
    val updated_ad: String,
    val nama: String,
    val gambar: String,
    val poster: String,
    val deskripsi: String,
    val sumber: String,
    val start: String,
    val deadline: String
) : Parcelable