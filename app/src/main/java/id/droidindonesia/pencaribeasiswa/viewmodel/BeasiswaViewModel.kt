package id.droidindonesia.pencaribeasiswa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import id.droidindonesia.pencaribeasiswa.model.Beasiswa
import id.droidindonesia.pencaribeasiswa.repository.MainRepository

class BeasiswaViewModel(application: Application) : AndroidViewModel(application) {

  var mainRepo: MainRepository? = null

  private lateinit var beasiswa: Beasiswa


  fun getBeasiswa(id: Int, callback: (Beasiswa?) -> Unit) {
    mainRepo?.getBeasiswa(id) { beasiswa ->
      if (beasiswa == null) {
        callback(null)
      } else {
        Log.i("BeasiswaViewModel", beasiswa.toString())
        this.beasiswa = beasiswa
        callback(beasiswa)
      }
    }
  }

  fun saveFavBeasiswa(callback: (String?) -> Unit) {
//    beasiswaDao = db?.beassiwaDao()
//
//    with(beasiswaDao) {
//      this?.insert(beasiswa)
//      callback("Berhasil di simpan.")
//    }
  }

  fun loadFavBeasiswa(callback: (List<Beasiswa>?) -> Unit) {
//    beasiswaDao = db?.beassiwaDao()
//    with(beasiswaDao) {
//      val favBeasiswa = this?.getAll()
//      callback(favBeasiswa)
//    }
  }
}
