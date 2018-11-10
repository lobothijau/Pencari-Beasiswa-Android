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

package id.droidindonesia.pencaribeasiswa.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.service.ListBeasiswaResponse
import java.text.SimpleDateFormat
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import android.widget.RatingBar
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.formats.NativeAd


class BeasiswaListAdapter(private var listBeasiswa: List<Any>?,
                          private val podcastListAdapterListener: PodcastListAdapterListener,
                          private val parentActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  // A beasiswa item view type.
  private val BEASISWA_ITEM_VIEW_TYPE = 0

  // The unified native ad view type.
  private val UNIFIED_NATIVE_AD_VIEW_TYPE = 1

  interface PodcastListAdapterListener {
    fun onShowDetails(beasiswa: ListBeasiswaResponse.Beasiswa?)
  }

  override fun getItemViewType(position: Int): Int {
    val rvItem = listBeasiswa?.get(position)
    if (rvItem is UnifiedNativeAd) {
      return UNIFIED_NATIVE_AD_VIEW_TYPE
    }
    return BEASISWA_ITEM_VIEW_TYPE
  }

  inner class ViewHolder(v: View, private val podcastListAdapterListener: PodcastListAdapterListener) : RecyclerView.ViewHolder(v) {

    val namaBeasiswaTextView: TextView = v.findViewById(R.id.namaBeasiswa)
    val negaraBeasiswaTextView: TextView = v.findViewById(R.id.negaraTextView)
    val gambarBeasiswaImageView: ImageView = v.findViewById(R.id.gambarBeasiswa)
    val tanggalDeadlineTextView: TextView = v.findViewById(R.id.tanggalDeadline)
    val bulanDeadlineTextView: TextView = v.findViewById(R.id.bulanDeadline)

    init {
      v.setOnClickListener {
        podcastListAdapterListener.onShowDetails(listBeasiswa!![adapterPosition] as ListBeasiswaResponse.Beasiswa)
      }
    }
  }

  inner class UnifiedNativeAdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

    val adView: UnifiedNativeAdView

    init {
      adView = view.findViewById<View>(R.id.ad_view) as UnifiedNativeAdView

      // The MediaView will display a video asset if one is present in the ad, and the
      // first image asset otherwise.
      adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

      // Register the view used for each individual asset.
      adView.headlineView = adView.findViewById(R.id.ad_headline)
      adView.bodyView = adView.findViewById(R.id.ad_body)
      adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
      adView.iconView = adView.findViewById(R.id.ad_icon)
      adView.priceView = adView.findViewById(R.id.ad_price)
      adView.starRatingView = adView.findViewById(R.id.ad_stars)
      adView.storeView = adView.findViewById(R.id.ad_store)
      adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
    }
  }

  fun setSearchData(listBeasiswaFromServer: List<Any>) {
    this.listBeasiswa = listBeasiswaFromServer
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): RecyclerView.ViewHolder {

    when (viewType) {
      UNIFIED_NATIVE_AD_VIEW_TYPE -> {
        val unifiedNativeLayoutView = LayoutInflater.from(
            parent.getContext()).inflate(R.layout.ad_unified,
            parent, false)
        return UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
      }
      BEASISWA_ITEM_VIEW_TYPE -> {
        val menuItemLayoutView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.search_item, parent, false)
        return ViewHolder(menuItemLayoutView, podcastListAdapterListener)
      }
      // Fall through.
      else -> {
        val menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false)
        return ViewHolder(menuItemLayoutView, podcastListAdapterListener)
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val viewType = getItemViewType(position)

    if (viewType == UNIFIED_NATIVE_AD_VIEW_TYPE) {
      val nativeAd = listBeasiswa?.get(position) as UnifiedNativeAd
      populateNativeAdView(nativeAd, (holder as UnifiedNativeAdViewHolder).adView)
      return
    }

    val searchView = listBeasiswa?.get(position) as ListBeasiswaResponse.Beasiswa

    val format = SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse(searchView.deadline)
    val day = DateFormat.format("dd", date) as String // 20
    val monthString = DateFormat.format("MMM", date) as String // Jun
    val yearString = DateFormat.format("yy", date) as String // 19

    val vh = holder as ViewHolder
    holder.namaBeasiswaTextView.text = searchView.nama
    holder.negaraBeasiswaTextView.text = searchView.negara.get(0) ?: "-"
    holder.bulanDeadlineTextView.text = "$day $monthString $yearString";
    Glide.with(parentActivity)
        .load(searchView.gambar)
        .apply(RequestOptions().placeholder(R.drawable.isola))
        .into(holder.gambarBeasiswaImageView)
  }

  override fun getItemCount(): Int {
    return listBeasiswa?.size ?: 0
  }

  private fun populateNativeAdView(nativeAd: UnifiedNativeAd,
                                   adView: UnifiedNativeAdView) {
    // Some assets are guaranteed to be in every UnifiedNativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    (adView.bodyView as TextView).text = nativeAd.body
    (adView.callToActionView as Button).setText(nativeAd.callToAction)

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    val icon = nativeAd.icon

    if (icon == null) {
      adView.iconView.visibility = View.INVISIBLE
    } else {
      (adView.iconView as ImageView).setImageDrawable(icon.drawable)
      adView.iconView.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adView.priceView.visibility = View.INVISIBLE
    } else {
      adView.priceView.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView.visibility = View.INVISIBLE
    } else {
      adView.storeView.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adView.advertiserView.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAd)
  }
}
