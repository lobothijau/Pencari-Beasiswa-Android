package id.droidindonesia.pencaribeasiswa.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artikel(
    val id: Int,
    val created_at: String,
    val updated_at: String,
    val judul: String,
    val gambar: String,
    val isi: String
) : Parcelable