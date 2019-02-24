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
import id.droidindonesia.pencaribeasiswa.model.Lomba
import java.text.SimpleDateFormat


class LombaListAdapter(private var items: List<Lomba>?,
                       private val lombaListAdapterListener: LombaAdapterListener,
                       private val parentActivity: Activity) :
    androidx.recyclerview.widget.RecyclerView.Adapter<LombaListAdapter.LombaViewHolder>() {

  interface LombaAdapterListener {
    fun onShowLomba(lomba: Lomba?)
  }

  inner class LombaViewHolder(v: View, private val lombaAdapterListener: LombaAdapterListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

    val namaLomba: TextView = v.findViewById(R.id.namaLomba)
    val tanggalDeadline: TextView = v.findViewById(R.id.tanggalDeadline)
    val gambarLomba: ImageView = v.findViewById(R.id.gambarLomba)
    val kategoriLomba: TextView = v.findViewById(R.id.kategori)

    init {
      v.setOnClickListener {
        lombaAdapterListener.onShowLomba(items!![adapterPosition])
      }
    }
  }

  fun setData(listLombaFromServer: List<Lomba>) {
    this.items = listLombaFromServer
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): LombaViewHolder {


    val menuItemLayoutView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.lomba_item, parent, false)
    return LombaViewHolder(menuItemLayoutView, lombaListAdapterListener)

  }


  override fun onBindViewHolder(holder: LombaViewHolder, position: Int) {
    val lomba = items?.get(position) as Lomba

    val format = SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse(lomba.deadline)
    val day = DateFormat.format("dd", date) as String // 20
    val monthString = DateFormat.format("MMM", date) as String // Jun
    val yearString = DateFormat.format("yy", date) as String // 19

    val vh = holder as LombaViewHolder
    holder.namaLomba.text = lomba.nama
    holder.kategoriLomba.text = lomba.kategori
    holder.tanggalDeadline.text = "$day $monthString $yearString";
    Glide.with(parentActivity)
        .load(lomba.gambar)
        .apply(RequestOptions().placeholder(R.drawable.isola))
        .into(holder.gambarLomba)
  }

  override fun getItemCount(): Int {
    return items?.size ?: 0
  }

  fun clear() {
    items = emptyList()
    notifyDataSetChanged()
  }
}
