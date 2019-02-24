package id.droidindonesia.pencaribeasiswa.service

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListBeasiswaResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<Beasiswa>
) : Parcelable {
  @Parcelize
  data class Beasiswa(
      val id: Int,
      val nama: String,
      val gambar: String,
      val start: String,
      val deadline: String,
      val negara: List<String>
  ) : Parcelable
}