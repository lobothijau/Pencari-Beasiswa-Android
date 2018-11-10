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

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.adapter.BeasiswaListAdapter
import id.droidindonesia.pencaribeasiswa.repository.BeasiswaRepository
import id.droidindonesia.pencaribeasiswa.service.BeasiswaService
import id.droidindonesia.pencaribeasiswa.service.ListBeasiswaResponse
import id.droidindonesia.pencaribeasiswa.viewmodel.BeasiswaViewModel
import id.droidindonesia.pencaribeasiswa.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_beasiswa.*
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import id.droidindonesia.pencaribeasiswa.adapter.ArtikelListAdapter
import id.droidindonesia.pencaribeasiswa.model.Artikel


class BeasiswaActivity : AppCompatActivity(), BeasiswaListAdapter.PodcastListAdapterListener, ArtikelListAdapter.ArtikelListAdapterListener {

  override fun onDetailArtikel(artikel: Artikel?) {
    val artikelDetailsFragment = ArtikelDetailsFragment.newInstance(artikel)
    supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer,
        artikelDetailsFragment, TAG_ARTIKEL_FRAGMENT).addToBackStack("ArtikelDetailsFragment").commit()
    podcastRecyclerView.visibility = View.INVISIBLE
    bottomNavigation.visibility = View.INVISIBLE
    searchMenuItem.isVisible = false
  }

  private lateinit var searchViewModel: SearchViewModel
  private lateinit var beasiswaViewModel: BeasiswaViewModel
  private lateinit var beasiswaListAdapter: BeasiswaListAdapter
  private lateinit var searchMenuItem: MenuItem
  private lateinit var beasiswaList: MutableList<Any>

  private lateinit var adLoader: AdLoader
  private lateinit var nativeAds: MutableList<UnifiedNativeAd>

  private lateinit var artikelAdapter: ArtikelListAdapter


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_beasiswa)
    setupToolbar()
    setupBottomNavigation()


    setupViewModels()
    updateControls()
    handleIntent(intent)
    addBackStackListener()

    artikelAdapter = ArtikelListAdapter(emptyList(), this, this)

    beasiswaList = mutableListOf()
    nativeAds = mutableListOf()

    // Initial search
    performSearch("")
  }

  private fun setupBottomNavigation() {
    bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
  }

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.menu_beasiswa -> {
        loadBeasiswaRecyclerView()
        return@OnNavigationItemSelectedListener true
      }
      R.id.menu_artikel -> {
        loadArtikelRecyclerView()
        return@OnNavigationItemSelectedListener true
      }
//      R.id.menu_favorit -> {
//        loadFavoritRecyclerView()
//        return@OnNavigationItemSelectedListener true
//      }
    }
    false
  }


  private fun loadBeasiswaRecyclerView() {
    if (searchViewModel.listBeasiswa.isNotEmpty()) {
      beasiswaListAdapter.setSearchData(searchViewModel.listBeasiswa)
      podcastRecyclerView.adapter = beasiswaListAdapter
    }
  }

  private fun loadArtikelRecyclerView() {
    if (searchViewModel.listArtikel.isNotEmpty()) {
      artikelAdapter.setData(searchViewModel.listArtikel)
    } else {
      progressBar.visibility = View.VISIBLE
      searchViewModel.searchArtikel {
        if (it == null) {
          showError("Data tidak tersedia.")
        } else {
          artikelAdapter.setData(it)
        }
        progressBar.visibility = View.INVISIBLE
      }
    }
    podcastRecyclerView.adapter = artikelAdapter
  }

  private fun loadFavoritRecyclerView() {
    beasiswaViewModel.loadFavBeasiswa {
      beasiswaListAdapter.setSearchData(it!!)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {

    if (item?.itemId == R.id.menu_simpan) {
      beasiswaViewModel.saveFavBeasiswa {
        showToast(it)
        searchMenuItem.isVisible = false
      }
    }

    return super.onOptionsItemSelected(item)
  }

  private fun showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.menu_search, menu)

    searchMenuItem = menu.findItem(R.id.search_item)
    val searchView = searchMenuItem.actionView as SearchView

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

    if (supportFragmentManager.backStackEntryCount > 0) {
      podcastRecyclerView.visibility = View.INVISIBLE
    }

    if (podcastRecyclerView.visibility == View.INVISIBLE) {
      searchMenuItem.isVisible = false
    }

    return true
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  override fun onShowDetails(beasiswa: ListBeasiswaResponse.Beasiswa?) {
    if (beasiswa != null) {
      showDetailsFragment(beasiswa)
    }
  }

  private fun insertAdsInMenuItems() {
    Log.d("BeasiswaActivity", "Jumlah naive ads: $nativeAds")
    if (nativeAds.isEmpty()) {
      return
    }

    val offset = beasiswaList.size / nativeAds.size + 1
    var index = 0
    for (ad in nativeAds) {
      beasiswaList.add(index, ad)
      index += offset
    }
  }

  private fun loadNativeAds(count: Int) {

    val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id))
    adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
      // A native ad loaded successfully, check if the ad loader has finished loading
      // and if so, insert the ads into the list.
      nativeAds.add(unifiedNativeAd)
      if (!adLoader.isLoading) {
        insertAdsInMenuItems()
      }
    }.withAdListener(
        object : AdListener() {
          override fun onAdFailedToLoad(errorCode: Int) {
            // A native ad failed to load, check if the ad loader has finished loading
            // and if so, insert the ads into the list.
            Log.e("BeasiswaActivity", ("The previous native ad failed to load. Attempting to" + " load another."))
            if (!adLoader.isLoading) {
              insertAdsInMenuItems()
            }
          }
        }).build()

    // Load the Native Express ad. Load per 8 items.
    adLoader.loadAds(AdRequest.Builder().build(), count / 8)
  }

  private fun performSearch(q: String) {
    showProgressBar()
    searchViewModel.searchPodcasts(q) { beasiswaResponse ->
      hideProgressBar()
      // Ganti title hanya jika user memang melakukan pencarian
      if (!q.isEmpty()) {
        toolbar.title = getString(R.string.search_results)
      }
      Log.d("BeasiswaActivity", "Beasiswa Response: $beasiswaResponse")
      if (beasiswaResponse != null) {
        beasiswaList.addAll(beasiswaResponse)
        Log.d("BeasiswaActivity", "Jumlah beasiswa: $beasiswaList")
        loadNativeAds(beasiswaList.size)
      }
      beasiswaListAdapter.setSearchData(beasiswaList)
    }
  }

  private fun handleIntent(intent: Intent) {
    if (Intent.ACTION_SEARCH == intent.action) {
      val query = intent.getStringExtra(SearchManager.QUERY)
      performSearch(query)
    }
  }


  private fun setupToolbar() {
    setSupportActionBar(toolbar)
  }

  private fun setupViewModels() {
    val service = BeasiswaService.instance
    val repo = BeasiswaRepository(service)
    searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    searchViewModel.beasiswaRepo = repo
    beasiswaViewModel = ViewModelProviders.of(this).get(BeasiswaViewModel::class.java)
    beasiswaViewModel.beasiswaRepo = repo
  }

  private fun addBackStackListener() {
    supportFragmentManager.addOnBackStackChangedListener {
      if (supportFragmentManager.backStackEntryCount == 0) {
        podcastRecyclerView.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
        toolbar.title = getString(R.string.app_name)

      }
    }
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount == 0) {
      confirmExit()
    } else {
      super.onBackPressed()
    }
  }

  private fun confirmExit() {
    AlertDialog.Builder(this)
        .setMessage("Anda yakin ingin keluar?")
        .setPositiveButton("Ya") { dialog, which -> finish() }
        .setNegativeButton("Tidak", null)
        .show()
  }

  private fun updateControls() {
    podcastRecyclerView.setHasFixedSize(true)

    val layoutManager = LinearLayoutManager(this)
    podcastRecyclerView.layoutManager = layoutManager

    val dividerItemDecoration = android.support.v7.widget.DividerItemDecoration(
        podcastRecyclerView.context, layoutManager.orientation)
    podcastRecyclerView.addItemDecoration(dividerItemDecoration)

    beasiswaListAdapter = BeasiswaListAdapter(null, this, this)
    podcastRecyclerView.adapter = beasiswaListAdapter
  }


  private fun showDetailsFragment(beasiswa: ListBeasiswaResponse.Beasiswa) {
    val beasiswaDetailsFragment = createBeasiswaDetailFragment(beasiswa)

    supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer,
        beasiswaDetailsFragment, TAG_DETAILS_FRAGMENT).addToBackStack("DetailsFragment").commit()
    podcastRecyclerView.visibility = View.INVISIBLE
    bottomNavigation.visibility = View.INVISIBLE
    searchMenuItem.isVisible = false

  }

  private fun createBeasiswaDetailFragment(beasiswa: ListBeasiswaResponse.Beasiswa): BeasiswaDetailsFragment {
    var beasiswaDetailsFragment = supportFragmentManager.findFragmentByTag(TAG_DETAILS_FRAGMENT) as
        BeasiswaDetailsFragment?

    if (beasiswaDetailsFragment == null) {
      beasiswaDetailsFragment = BeasiswaDetailsFragment.newInstance(beasiswa)
    }

    return beasiswaDetailsFragment
  }

  private fun showProgressBar() {
    progressBar.visibility = View.VISIBLE
  }

  private fun hideProgressBar() {
    progressBar.visibility = View.INVISIBLE
  }

  private fun showError(message: String) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(getString(R.string.ok_button), null)
        .create()
        .show()
  }

  companion object {
    private val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    private val TAG_ARTIKEL_FRAGMENT = "ArtikelDetailsFragment"
  }
}
