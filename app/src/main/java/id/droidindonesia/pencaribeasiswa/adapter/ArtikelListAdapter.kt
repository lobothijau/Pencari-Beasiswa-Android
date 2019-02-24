package id.droidindonesia.pencaribeasiswa.adapter

import android.app.Activity
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.model.Artikel
import java.text.SimpleDateFormat


class ArtikelListAdapter(private var items: List<Any>?,
                         private val podcastListAdapterListener: ArtikelListAdapterListener,
                         private val parentActivity: Activity) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


  interface ArtikelListAdapterListener {
    fun onDetailArtikel(artikel: Artikel?)
  }

  inner class ViewHolder(v: View, private val podcastListAdapterListener: ArtikelListAdapterListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

    val judulTV: TextView = v.findViewById(R.id.titleTextView)
    val gambarIV: ImageView = v.findViewById(R.id.newsImageView)
    val tanggalTV: TextView = v.findViewById(R.id.dateTextView)


    init {
      v.setOnClickListener {
        podcastListAdapterListener.onDetailArtikel(items!![adapterPosition] as Artikel)
      }
    }
  }

  fun setData(listArtikelFromServer: List<Any>) {
    this.items = listArtikelFromServer
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {


    val menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artikel_item, parent, false)
    return ViewHolder(menuItemLayoutView, podcastListAdapterListener)

  }

  override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    val searchView = items?.get(position) as Artikel

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
        .apply(RequestOptions().placeholder(R.drawable.isola))
        .into(holder.gambarIV)
  }

  override fun getItemCount(): Int {
    return items?.size ?: 0
  }

  fun clear() {
    items = emptyList()
    notifyDataSetChanged()
  }
}
