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
import id.my.githubuser.data.local.entity.FavoriteUser
import id.my.githubuser.databinding.ActivityFavoritesBinding
import id.my.githubuser.ui.FavoriteAdapter
import id.my.githubuser.ui.viewmodel.MainViewModelFactory
import id.my.githubuser.ui.SettingPreferences
import id.my.githubuser.ui.viewmodel.FavoritesViewModel
import id.my.githubuser.ui.viewmodel.ViewModelFactory
import id.my.githubuser.ui.datastore
import id.my.githubuser.ui.viewmodel.MainViewModel

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var mainViewModel: MainViewModel
    private var isDarkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoritesViewModel = obtainViewModel(this@FavoritesActivity)

        val pref = SettingPreferences.getInstance(application.datastore)
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(pref)
        )[MainViewModel::class.java]

        // setting the recycler view (rvSearchResult)
        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFavorites.addItemDecoration(itemDecoration)

        favoritesViewModel.getAllFavorites().observe(this) {
            setFavoritesData(it)
        }

        // observe mainViewModel
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            setIconMenu(isDarkModeActive)
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            isDarkMode = isDarkModeActive
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mode -> {
                    mainViewModel.saveThemeSetting(!isDarkMode)
                    true
                }

                else -> false
            }
        }

    }

    private fun setFavoritesData(users: List<FavoriteUser>) {
        val adapter = FavoriteAdapter()
        adapter.submitList(users)
        binding.rvFavorites.adapter = adapter
        binding.progressBar.visibility = View.GONE

        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: FavoriteUser) {
                // Intent with data to DetailUserActivity
                val usernameProfile = data.username
                val detailUserIntent =
                    Intent(this@FavoritesActivity, DetailUserActivity::class.java)
                detailUserIntent.putExtra(DetailUserActivity.EXTRA_USER, usernameProfile)
                startActivity(detailUserIntent)
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setIconMenu(isDarkMode: Boolean) {
        val modeMenu = binding.topAppBar.menu.findItem(R.id.mode)

        if (isDarkMode) {
            modeMenu.icon = getDrawable(R.drawable.baseline_wb_sunny_24)
            modeMenu.title = "Light Mode"
        } else {
            modeMenu.icon = getDrawable(R.drawable.baseline_dark_mode_24)
            modeMenu.title = "Dark Mode"
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoritesViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoritesViewModel::class.java]
    }
}