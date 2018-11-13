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

package id.droidindonesia.pencaribeasiswa.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Html
import android.text.Spanned
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.R.id.*
import id.droidindonesia.pencaribeasiswa.model.BeasiswaList
import id.droidindonesia.pencaribeasiswa.service.ListBeasiswaResponse
import id.droidindonesia.pencaribeasiswa.viewmodel.BeasiswaViewModel
import id.droidindonesia.pencaribeasiswa.util.HtmlUtils
import id.droidindonesia.pencaribeasiswa.util.DateUtils
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import java.text.SimpleDateFormat
import io.fabric.sdk.android.services.settings.IconRequest.build
import kotlinx.android.synthetic.main.fragment_artikel_details.*


class BeasiswaDetailsFragment : Fragment() {

  private lateinit var beasiswaViewModel : BeasiswaViewModel

  companion object {
    fun newInstance(beasiswa: BeasiswaList): BeasiswaDetailsFragment {
      val beasiwaDetailsFragment = BeasiswaDetailsFragment()
      val args = Bundle()
      args.putParcelable("beasiswa", beasiswa)
      beasiwaDetailsFragment.arguments = args
      return beasiwaDetailsFragment
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setHasOptionsMenu(true)
    setupViewModel()
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater!!.inflate(R.layout.fragment_podcast_details, container, false)
  }

  lateinit var namaBeasiswa: TextView
  lateinit var adView: AdView
  lateinit var progressBar: ProgressBar
  lateinit var gambarBeasiswa: ImageView
  lateinit var deskripsiBeasiswa: TextView
  lateinit var deadlineTextView: TextView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val beasiswa: BeasiswaList = arguments?.get("beasiswa") as BeasiswaList

    namaBeasiswa = view.findViewById(R.id.namaBeasiswa)
    adView = view.findViewById(R.id.adView)
    progressBar = view.findViewById(R.id.progressBar)
    gambarBeasiswa = view.findViewById(R.id.gambarBeasiswa)
    deskripsiBeasiswa = view.findViewById(R.id.deskripsiBeasiswa)
    deadlineTextView = view.findViewById(R.id.deadlineTextView)

    namaBeasiswa.text = beasiswa.nama
    activity?.let { Glide.with(it).load(beasiswa.gambar).into(gambarBeasiswa) }

    getDetailBeasiswa(beasiswa.id)

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }

  private fun getDetailBeasiswa(id: Int) {
    beasiswaViewModel.getBeasiswa(id) {beasiswa ->
      progressBar.visibility = View.GONE

      if (beasiswa == null) {
        Toast.makeText(activity, "Data beasiswa tidak berhasil dimuat.", Toast.LENGTH_SHORT).show()
      } else {
        deskripsiBeasiswa.text = HtmlUtils.htmlToSpannable(beasiswa.deskripsi)
        deskripsiBeasiswa.movementMethod = LinkMovementMethod.getInstance()

        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(beasiswa.deadline)
        val day = DateFormat.format("dd", date) as String // 20
        val monthString = DateFormat.format("MMM", date) as String // Jun
        val yearString = DateFormat.format("yyyy", date) as String // 2018
        deadlineTextView.text = "Deadline: $day $monthString $yearString"
      }
    }
  }

//  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//    super.onCreateOptionsMenu(menu, inflater)
//    inflater?.inflate(R.menu.menu_details, menu)
//  }

  private fun setupViewModel() {
    beasiswaViewModel = activity?.let { ViewModelProviders.of(it).get(BeasiswaViewModel::class.java) }!!
  }

}