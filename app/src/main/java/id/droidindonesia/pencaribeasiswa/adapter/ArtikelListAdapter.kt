package id.droidindonesia.pencaribeasiswa.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.model.Artikel
import id.droidindonesia.pencaribeasiswa.service.ListBeasiswaResponse
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import java.text.SimpleDateFormat


class ArtikelListAdapter(private var listArtikel: List<Any>?,
                         private val podcastListAdapterListener: ArtikelListAdapterListener,
                         private val parentActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


  interface ArtikelListAdapterListener {
    fun onDetailArtikel(artikel: Artikel?)
  }

  inner class ViewHolder(v: View, private val podcastListAdapterListener: ArtikelListAdapterListener) : RecyclerView.ViewHolder(v) {

    val judulTV: TextView = v.findViewById(R.id.titleTextView)
    val gambarIV: ImageView = v.findViewById(R.id.newsImageView)
    val tanggalTV: TextView = v.findViewById(R.id.dateTextView)


    init {
      v.setOnClickListener {
        podcastListAdapterListener.onDetailArtikel(listArtikel!![adapterPosition] as Artikel)
      }
    }
  }

  fun setData(listArtikelFromServer: List<Any>) {
    this.listArtikel = listArtikelFromServer
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): RecyclerView.ViewHolder {


    val menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artikel_item, parent, false)
    return ViewHolder(menuItemLayoutView, podcastListAdapterListener)

  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val searchView = listArtikel?.get(position) as Artikel

    val format = SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse(searchView.created_at)
    val day = DateFormat.format("dd", date) as String // 20
    val monthString = DateFormat.format("MMM", date) as String // Jun
    val yearString = DateFormat.format("yyyy", date) as String // 2019

    val myholder = holder as ViewHolder

    myholder.judulTV.text = searchView.judul
    myholder.tanggalTV.text = "$day $monthString $yearString";
    Glide.with(parentActivity)
        .load(searchView.gambar)
        .into(holder.gambarIV)
  }

  override fun getItemCount(): Int {
    return listArtikel?.size ?: 0
  }

}
