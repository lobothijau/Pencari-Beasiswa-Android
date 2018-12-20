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
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.adapter.BeasiswaListAdapter
import id.droidindonesia.pencaribeasiswa.repository.MainRepository
import id.droidindonesia.pencaribeasiswa.service.BeasiswaService
import id.droidindonesia.pencaribeasiswa.viewmodel.BeasiswaViewModel
import id.droidindonesia.pencaribeasiswa.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_beasiswa.*
import com.google.android.gms.ads.AdLoader
import id.droidindonesia.pencaribeasiswa.adapter.ArtikelListAdapter
import id.droidindonesia.pencaribeasiswa.adapter.LombaListAdapter
import id.droidindonesia.pencaribeasiswa.model.Artikel
import id.droidindonesia.pencaribeasiswa.model.BeasiswaList
import id.droidindonesia.pencaribeasiswa.model.Lomba
import android.content.DialogInterface
import androidx.core.view.MenuItemCompat
import com.thefinestartist.finestwebview.FinestWebView


class BeasiswaActivity : AppCompatActivity(), BeasiswaListAdapter.PodcastListAdapterListener, ArtikelListAdapter.ArtikelListAdapterListener, LombaListAdapter.LombaAdapterListener {
  override fun onShowLomba(lomba: Lomba?) {
    if (lomba != null || lomba?.sumber?.isNotEmpty()!!) {
      FinestWebView.Builder(this)
          .titleDefault("")
          .progressBarColorRes(R.color.colorAccent)
          .swipeRefreshColorRes(R.color.colorAccent)
          .webViewSupportZoom(true)
          .webViewDisplayZoomControls(true)
          .webViewBuiltInZoomControls(true)
          .show(lomba.sumber)
    } else {
      Toast.makeText(this, "Informasi lomba tidak tersedia", Toast.LENGTH_SHORT).show()
    }
    //    val lombaDetailsFragment = LombaDetailsFragment.newInstance(lomba)
    //    supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer,
    //        lombaDetailsFragment, TAG_LOMBA_FRAGMENT).addToBackStack(TAG_LOMBA_FRAGMENT).commit()
    //    hideListAndBottomNavigation()
  }

  fun hideListAndBottomNavigation() {
    swipe.visibility = View.GONE
    podcastRecyclerView.visibility = View.GONE
    bottomNavigation.visibility = View.INVISIBLE
    searchMenuItem.isVisible = false
    fab.visibility = View.GONE
  }

  override fun onDetailArtikel(artikel: Artikel?) {
    val artikelDetailsFragment = ArtikelDetailsFragment.newInstance(artikel)
    supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer,
        artikelDetailsFragment, TAG_ARTIKEL_FRAGMENT).addToBackStack("ArtikelDetailsFragment").commit()
    hideListAndBottomNavigation()
  }

  private lateinit var mainViewModel: MainViewModel
  private lateinit var beasiswaViewModel: BeasiswaViewModel
  private lateinit var beasiswaListAdapter: BeasiswaListAdapter
  private lateinit var searchMenuItem: MenuItem

  private lateinit var adLoader: AdLoader

  private lateinit var artikelAdapter: ArtikelListAdapter
  private lateinit var lombaListAdapter: LombaListAdapter

  private var nama = ""
  private lateinit var searchView: SearchView

//  private var selectedMenu : Int = -1

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
    lombaListAdapter = LombaListAdapter(emptyList(), this, this)

    // Initial search
    performSearch("")

    swipe.setOnRefreshListener {
      val selectedMenu = bottomNavigation.selectedItemId

      if (selectedMenu == R.id.menu_beasiswa) {
        mainViewModel.listBeasiswa = emptyList()
        performSearch("")
      } else if (selectedMenu == R.id.menu_lomba) {
        mainViewModel.listLomba = emptyList()
        loadLombaRecyclerView("")
      } else if (selectedMenu == R.id.menu_artikel) {
        mainViewModel.listArtikel = emptyList()
        loadArtikelRecyclerView("")
      }
    }

    fab.setOnClickListener {
      val builder = AlertDialog.Builder(this@BeasiswaActivity)

      val entries = arrayOf("Luar Negeri", "D3", "S1", "S2", "S3", "Dalam Negeri")
      val entryValues = emptyArray<Int>()

      val checkBoolean = booleanArrayOf(false,
          false,
          false,
          false,
          false,
          false
      )
      builder.setTitle("Filter berdasarkan: ")
          // Specify the list array, the items to be selected by default (null for none),
          // and the listener through which to receive callbacks when items are selected
          .setMultiChoiceItems(entries, checkBoolean) { dialog, which, isChecked ->
            checkBoolean[which] = isChecked
          }
          // Set the action buttons
          .setPositiveButton("OK") { dialog, id ->
            // User clicked OK, so save the checkedItems results somewhere
            // or return them to the component that opened the dialog
            //...
            searchBeasiswaWithFilter(checkBoolean)
          }
          .setNegativeButton("BATAL", DialogInterface.OnClickListener { dialog, id ->
            //...
          })
          .create()
          .show()
    }
  }

  private fun setupBottomNavigation() {
    bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
  }

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.menu_beasiswa -> {
        loadBeasiswaRecyclerView()
//        selectedMenu = R.id.menu_beasiswa
        fab.visibility = View.VISIBLE
        searchMenuItem.isVisible = true
        return@OnNavigationItemSelectedListener true
      }
      R.id.menu_artikel -> {
        loadArtikelRecyclerView("")
//        selectedMenu = R.id.menu_artikel
        fab.visibility = View.GONE
        searchMenuItem.isVisible = false
        return@OnNavigationItemSelectedListener true
      }
      R.id.menu_lomba -> {
        loadLombaRecyclerView("")
//        selectedMenu = R.id.menu_lomba
        fab.visibility = View.GONE
        searchMenuItem.isVisible = false
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }


  private fun loadBeasiswaRecyclerView() {
    if (mainViewModel.listBeasiswa.isNotEmpty()) {
      beasiswaListAdapter.setSearchData(mainViewModel.listBeasiswa)
      podcastRecyclerView.adapter = beasiswaListAdapter
    }
  }

  private fun loadArtikelRecyclerView(query: String) {
    if (mainViewModel.listArtikel.isNotEmpty()) {
      artikelAdapter.setData(mainViewModel.listArtikel)
    } else {
      progressBar.visibility = View.VISIBLE
      mainViewModel.searchArtikel {
        if (it == null) {
          showError("Data tidak tersedia. Pastikan memiliki koneksi Internet yang baik.")
        } else {
          artikelAdapter.setData(it)
        }
        progressBar.visibility = View.INVISIBLE
      }
    }
    podcastRecyclerView.adapter = artikelAdapter
    swipe.isRefreshing = false;
  }

  private fun loadLombaRecyclerView(query: String) {
    if (mainViewModel.listLomba.isNotEmpty()) {
      lombaListAdapter.setData(mainViewModel.listLomba)
    } else {
      progressBar.visibility = View.VISIBLE
      mainViewModel.searchLomba(query) {
        if (it == null) {
          showError("Data tidak tersedia. Pastikan memiliki koneksi Internet yang baik.")
        } else {
          lombaListAdapter.setData(it)
        }
        progressBar.visibility = View.INVISIBLE
      }
    }
    podcastRecyclerView.adapter = lombaListAdapter
    swipe.isRefreshing = false;
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {

    if (item?.itemId == R.id.menu_simpan) {
      beasiswaViewModel.saveFavBeasiswa {
        showToast(it)
        searchMenuItem.isVisible = false
      }
    } else if (item?.itemId == R.id.contact_us) {
      val email = Intent(Intent.ACTION_SEND)
      email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("coolest.studio@gmail.com"))
      email.putExtra(Intent.EXTRA_SUBJECT, "Tulis judul pesan anda.")
      email.putExtra(Intent.EXTRA_TEXT, "Tulis isi pesan anda.")

      //need this to prompts email client only
      email.type = "message/rfc822"

      startActivity(Intent.createChooser(email, "Kirim dengan :"))
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
    searchView = searchMenuItem.actionView as SearchView

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

    if (supportFragmentManager.backStackEntryCount > 0) {
      swipe.visibility = View.GONE
      podcastRecyclerView.visibility = View.INVISIBLE
    }

    searchMenuItem.isVisible = podcastRecyclerView.visibility != View.INVISIBLE

    return true
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  override fun onShowDetails(beasiswa: BeasiswaList?) {
    if (beasiswa != null) {
      FinestWebView.Builder(this)
          .titleDefault("")
          .progressBarColorRes(R.color.colorAccent)
          .swipeRefreshColorRes(R.color.colorAccent)
          .webViewSupportZoom(true)
          .webViewDisplayZoomControls(true)
          .webViewBuiltInZoomControls(true)
          .show(beasiswa.sumber)


      // showDetailsFragment(beasiswa)
    } else {
      Toast.makeText(this, "Data beasiswa tidak tersedia.", Toast.LENGTH_SHORT).show()
    }
  }

  private fun searchBeasiswaWithFilter(checkBoolean: BooleanArray) {
    var listString = ArrayList<String>()
    if (checkBoolean[0])
      listString.add("1")
    if (checkBoolean[1])
      listString.add("6")
    if (checkBoolean[2])
      listString.add("2")
    if (checkBoolean[3])
      listString.add("3")
    if (checkBoolean[4])
      listString.add("4")
    if (checkBoolean[5])
      listString.add("5")


    Log.d("BeasiswaActivity", "Hasil" + listString.joinToString(separator = ","))

    mainViewModel.searchPodcastsByJenis(nama, listString.joinToString(separator = ",")) { beasiswaResponse ->

      // tutup search view
      if (!searchView.isIconified) {
        MenuItemCompat.collapseActionView(searchMenuItem);
      }
      // reset search
      nama = ""

      hideProgressBar()
      // Ganti title hanya jika user memang melakukan pencarian
      Log.d("BeasiswaActivity", "Beasiswa Response: $beasiswaResponse")
      if (beasiswaResponse != null) {
        beasiswaListAdapter.clear()
        beasiswaListAdapter.setSearchData(mainViewModel.listBeasiswa)
      }
      swipe.isRefreshing = false;

    }
  }

  private fun performSearch(q: String) {
    showProgressBar()
    mainViewModel.searchPodcasts(q) { beasiswaResponse ->
      hideProgressBar()
      // Ganti title hanya jika user memang melakukan pencarian
      if (!q.isEmpty()) {
        toolbar.title = getString(R.string.search_results)
      }
      // nama supaya  bisa di filter
      nama = q
      Log.d("BeasiswaActivity", "Beasiswa Response: $beasiswaResponse")
      if (beasiswaResponse != null) {
        beasiswaListAdapter.clear()
        beasiswaListAdapter.setSearchData(mainViewModel.listBeasiswa)
      }
      swipe.isRefreshing = false;
    }
  }

  private fun handleIntent(intent: Intent) {
    if (Intent.ACTION_SEARCH == intent.action) {
      val query = intent.getStringExtra(SearchManager.QUERY)

      val idBottomNav = bottomNavigation.selectedItemId
      if (idBottomNav == R.id.menu_beasiswa) {
        performSearch(query)
      } else if (idBottomNav == R.id.menu_artikel) {
        loadArtikelRecyclerView(query)
      } else if (idBottomNav == R.id.menu_lomba) {
        loadLombaRecyclerView(query)
      }
    }
  }


  private fun setupToolbar() {
    setSupportActionBar(toolbar)
  }

  private fun setupViewModels() {
    val service = BeasiswaService.instance
    val repo = MainRepository(service)
    mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    mainViewModel.mainRepo = repo
    beasiswaViewModel = ViewModelProviders.of(this).get(BeasiswaViewModel::class.java)
    beasiswaViewModel.mainRepo = repo
  }

  private fun addBackStackListener() {
    supportFragmentManager.addOnBackStackChangedListener {
      if (supportFragmentManager.backStackEntryCount == 0) {
        swipe.visibility = View.VISIBLE
        podcastRecyclerView.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
        searchMenuItem.isVisible = true
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

    val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    podcastRecyclerView.layoutManager = layoutManager

    val dividerItemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
        podcastRecyclerView.context, layoutManager.orientation)
    podcastRecyclerView.addItemDecoration(dividerItemDecoration)

    beasiswaListAdapter = BeasiswaListAdapter(null, this, this)
    podcastRecyclerView.adapter = beasiswaListAdapter
  }


  private fun showDetailsFragment(beasiswa: BeasiswaList) {
    val beasiswaDetailsFragment = createBeasiswaDetailFragment(beasiswa)

    supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer,
        beasiswaDetailsFragment, TAG_DETAILS_FRAGMENT).addToBackStack("DetailsFragment").commit()
    hideListAndBottomNavigation()
  }

  private fun createBeasiswaDetailFragment(beasiswa: BeasiswaList): BeasiswaDetailsFragment {
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
    private val TAG_LOMBA_FRAGMENT = "LombaDetailsFragment"
  }
}
