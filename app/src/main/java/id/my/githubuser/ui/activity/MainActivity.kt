package id.my.githubuser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.githubuser.R
import id.my.githubuser.data.remote.response.ItemsItem
import id.my.githubuser.databinding.ActivityMainBinding
import id.my.githubuser.ui.viewmodel.MainViewModel
import id.my.githubuser.ui.viewmodel.MainViewModelFactory
import id.my.githubuser.ui.ResultAdapter
import id.my.githubuser.ui.SettingPreferences
import id.my.githubuser.ui.datastore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var isDarkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.datastore)
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(pref)
        )[MainViewModel::class.java]

        binding.tvSearchResultTitle.text = getString(R.string.search_result, "dyandraaulia")

        supportActionBar?.hide()

        // observe live data
        mainViewModel.users.observe(this) { users ->
            setUsersData(users)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            setIconMenu(isDarkModeActive)
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            isDarkMode = isDarkModeActive
        }

        // setting the recycler view (rvSearchResult)
        val layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvSearchResult.addItemDecoration(itemDecoration)

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.favorite_page -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.mode -> {
                    mainViewModel.saveThemeSetting(!isDarkMode)
                    true
                }

                else -> false
            }
        }

        // search bar and search view setup
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText // Ganti dengan nama res/gaya Anda
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.textView.text = searchView.text
                    searchView.hide()
                    tvSearchResultTitle.text = getString(R.string.search_result, searchBar.text)

                    // set query request
                    mainViewModel.findUsers(searchBar.text.toString())
                    false
                }
        }
    }

    private fun setUsersData(users: List<ItemsItem>) {
        val adapter = ResultAdapter()
        adapter.submitList(users)
        binding.rvSearchResult.adapter = adapter

        adapter.setOnItemClickCallback(object : ResultAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                // Intent with data to DetailUserActivity
                val usernameProfile = data.login
                val detailUserIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
                detailUserIntent.putExtra(DetailUserActivity.EXTRA_USER, usernameProfile)
                startActivity(detailUserIntent)
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setIconMenu(isDarkMode: Boolean) {
        val modeMenu = binding.topAppBar.menu.findItem(R.id.mode)
        val favoriteMenu = binding.topAppBar.menu.findItem(R.id.favorite_page)

        if (isDarkMode) {
            modeMenu.icon = getDrawable(R.drawable.baseline_wb_sunny_24)
            favoriteMenu.icon = getDrawable(R.drawable.baseline_favorite_24)
            modeMenu.title = "Light Mode"
        } else {
            modeMenu.icon = getDrawable(R.drawable.baseline_dark_mode_24)
            favoriteMenu.icon = getDrawable(R.drawable.baseline_favorite_24_black)
            modeMenu.title = "Dark Mode"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}