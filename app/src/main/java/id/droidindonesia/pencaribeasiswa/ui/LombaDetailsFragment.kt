package id.droidindonesia.pencaribeasiswa.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textservice.TextInfo
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import id.droidindonesia.pencaribeasiswa.model.Lomba
import java.text.SimpleDateFormat
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.R.id.*
import kotlinx.android.synthetic.main.fragment_lomba_details.*
import id.droidindonesia.pencaribeasiswa.util.HtmlUtils
import id.droidindonesia.pencaribeasiswa.viewmodel.MainViewModel
import org.w3c.dom.Text


class LombaDetailsFragment : Fragment() {

  private lateinit var mainViewModel: MainViewModel
  private lateinit var progressBar: ProgressBar
  private lateinit var deskripsiLomba: TextView
  private lateinit var deadlineLomba: TextView
  private lateinit var poster: PhotoView

  companion object {
    fun newInstance(lomba: Lomba?): LombaDetailsFragment {
      val beasiwaDetailsFragment = LombaDetailsFragment()
      val args = Bundle()
      args.putParcelable("lomba", lomba)
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

    return inflater!!.inflate(R.layout.fragment_lomba_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val namaLomba = view.findViewById(R.id.namaLomba) as TextView
    val gambarLomba = view.findViewById(R.id.gambarLomba) as ImageView
    poster = view.findViewById(R.id.photoView) as PhotoView
    val adView = view.findViewById<AdView>(R.id.adView)
    progressBar = view.findViewById(R.id.progressBar)
    deadlineLomba = view.findViewById(R.id.deadlineTextView)
    deskripsiLomba = view.findViewById(R.id.deskripsiLomba)

    val lomba: Lomba = arguments?.get("lomba") as Lomba
    namaLomba.text = lomba.nama
    activity?.let {
      Glide.with(it).load(lomba.gambar).into(gambarLomba)
    }


    getDetailBeasiswa(lomba.id)

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }

  private fun getDetailBeasiswa(id: Int) {
    mainViewModel.getLomba(id) { lomba ->
      progressBar.visibility = View.GONE

      if (lomba == null) {
        Toast.makeText(activity, "Data lomba tidak berhasil dimuat.", Toast.LENGTH_SHORT).show()
      } else {
        deskripsiLomba.text = HtmlUtils.htmlToSpannable(lomba.deskripsi)

        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(lomba.deadline)
        val day = DateFormat.format("dd", date) as String // 20
        val monthString = DateFormat.format("MMM", date) as String // Jun
        val yearString = DateFormat.format("yyyy", date) as String // 2018
        deadlineLomba.text = "Deadline: $day $monthString $yearString"

        activity?.let {
          Glide.with(it).load(lomba.poster).into(poster)
        }
      }
    }
  }

//  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//    super.onCreateOptionsMenu(menu, inflater)
//    inflater?.inflate(R.menu.menu_details, menu)
//  }

  private fun setupViewModel() {
    mainViewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }!!
  }

}