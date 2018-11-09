package id.droidindonesia.pencaribeasiswa.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.model.Artikel
import kotlinx.android.synthetic.main.fragment_artikel_details.*
import java.text.SimpleDateFormat
import id.droidindonesia.pencaribeasiswa.util.HtmlUtils
class ArtikelDetailsFragment : Fragment() {

  companion object {
    fun newInstance(artikel: Artikel?): ArtikelDetailsFragment {
      val beasiwaDetailsFragment = ArtikelDetailsFragment()
      val args = Bundle()
      args.putParcelable("artikel", artikel)
      beasiwaDetailsFragment.arguments = args
      return beasiwaDetailsFragment
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater!!.inflate(R.layout.fragment_artikel_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val artikel: Artikel = arguments?.get("artikel") as Artikel
    isi.text = HtmlUtils.htmlToSpannable(artikel.isi)
    judul.text = artikel.judul
    val format = SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse(artikel.created_at)
    val day = DateFormat.format("dd", date) as String // 20
    val monthString = DateFormat.format("MMM", date) as String // Jun
    val yearString = DateFormat.format("yyyy", date) as String // 2019
    rilisTextView.text = "$day $monthString $yearString"

    activity?.let { Glide.with(it).load(artikel.gambar).into(gambarArtikel) }

  }

}