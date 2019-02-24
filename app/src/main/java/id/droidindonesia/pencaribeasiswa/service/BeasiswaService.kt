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

package id.droidindonesia.pencaribeasiswa.service

import id.droidindonesia.pencaribeasiswa.BuildConfig
import id.droidindonesia.pencaribeasiswa.model.Artikel
import id.droidindonesia.pencaribeasiswa.model.Beasiswa
import id.droidindonesia.pencaribeasiswa.model.BeasiswaList
import id.droidindonesia.pencaribeasiswa.model.Lomba
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BeasiswaService {
  @GET("beasiswa/all/")
  fun getAllBeasiswa(@Query("nama") nama: String): Call<List<BeasiswaList>>

  @GET("beasiswa/all/")
  fun getAllBeasiswaByJenis(@Query("nama") nama: String, @Query(value = "jenis") jenis: String): Call<List<BeasiswaList>>

  @GET("beasiswa/{id}/")
  fun getBeasiswa(@Path("id") id: Int): Call<Beasiswa>

  @GET("artikel/all/")
  fun getAllArtikel(): Call<List<Artikel>>

  @GET("artikel/{id}/")
  fun getArtikel(@Path("id") id: Int): Call<Artikel>

  @GET("lomba/all/")
  fun getAllLomba(@Query("q") q: String): Call<List<Lomba>>

  @GET("lomba/{id}/")
  fun getLomba(@Path("id") id: Int): Call<Lomba>

  companion object {
    val instance: BeasiswaService by lazy {
      val client = OkHttpClient().newBuilder()
          .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
          })
          .build()


      val retrofit = Retrofit.Builder()
          .client(client)
          .baseUrl("https://lobothijaju.pythonanywhere.com/api/v1/")
          .addConverterFactory(GsonConverterFactory.create())
          .build()
      retrofit.create<BeasiswaService>(BeasiswaService::class.java)
    }
  }
}