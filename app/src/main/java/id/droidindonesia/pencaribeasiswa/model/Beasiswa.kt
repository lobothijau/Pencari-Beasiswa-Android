package id.droidindonesia.pencaribeasiswa.model

data class Beasiswa(
    val id: Int,
    val negara: List<String>,
    val jenis: List<String>,
    val jurusan: List<String>,
    val universitas: List<String>,
    val created_at: String,
    val updated_ad: String,
    val nama: String,
    val gambar: String,
    val deskripsi: String,
    val sumber: String,
    val start: String,
    val deadline: String
)