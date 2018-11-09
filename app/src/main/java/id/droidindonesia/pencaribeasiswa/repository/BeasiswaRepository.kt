/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package id.droidindonesia.pencaribeasiswa.repository

import android.util.Log
import id.droidindonesia.pencaribeasiswa.model.Artikel
import id.droidindonesia.pencaribeasiswa.model.Beasiswa
import id.droidindonesia.pencaribeasiswa.service.BeasiswaService
import id.droidindonesia.pencaribeasiswa.service.ListBeasiswaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeasiswaRepository(private val beasiswaService: BeasiswaService) {

  fun getAllBeasiswa(q: String, callBack: (List<ListBeasiswaResponse.Beasiswa>?) -> Unit) {

    val podcastCall = beasiswaService.getAllBeasiswa(q)

    podcastCall.enqueue(object : Callback<List<ListBeasiswaResponse.Beasiswa>> {
      override fun onFailure(call: Call<List<ListBeasiswaResponse.Beasiswa>>?, t: Throwable?) {
        callBack(null)
      }

      override fun onResponse(call: Call<List<ListBeasiswaResponse.Beasiswa>>?, response: Response<List<ListBeasiswaResponse.Beasiswa>>?) {
        Log.d("BeasiswaRepository", "Inside Beasiswa Repository: ${response.toString()}")
        val body = response?.body()
        callBack(body)
      }
    })
  }

  fun getBeasiswa(id: Int, callback: (Beasiswa?) -> Unit) {
    val podcastCall = beasiswaService.getBeasiswa(id)

    podcastCall.enqueue(object: Callback<Beasiswa> {
      override fun onFailure(call: Call<Beasiswa>, t: Throwable) {
        callback(null)
      }

      override fun onResponse(call: Call<Beasiswa>?, response: Response<Beasiswa>?) {
        Log.d("BeasiswaRepository", "Beasiswa: ${response?.body().toString()}")
        val body = response?.body()
        callback(body)
      }
    })
  }

  fun getAllArtikel(callback: (List<Artikel>?) -> Unit) {
    val podcastCall = beasiswaService.getAllArtikel()

    podcastCall.enqueue(object : Callback<List<Artikel>> {
      override fun onFailure(call: Call<List<Artikel>>, t: Throwable) {
        callback(null)
      }

      override fun onResponse(call: Call<List<Artikel>>, response: Response<List<Artikel>>) {
        callback(response.body())
      }
    })
  }

  fun getArtikel(id: Int, callback: (Artikel?) -> Unit) {
    val podcastCall = beasiswaService.getArtikel(id)

    podcastCall.enqueue(object: Callback<Artikel> {
      override fun onFailure(call: Call<Artikel>, t: Throwable) {
        callback(null)
      }

      override fun onResponse(call: Call<Artikel>?, response: Response<Artikel>?) {
        Log.d("BeasiswaRepository", "Artikel: ${response?.body().toString()}")
        val body = response?.body()
        callback(body)
      }
    })
  }
}
