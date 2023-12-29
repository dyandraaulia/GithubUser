package id.my.githubuser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import id.my.githubuser.R
import id.my.githubuser.data.local.entity.FavoriteUser
import id.my.githubuser.data.remote.response.DetailUserResponse
import id.my.githubuser.databinding.ActivityDetailUserBinding
import id.my.githubuser.ui.viewmodel.MainViewModelFactory
import id.my.githubuser.ui.viewmodel.DetailUserViewModel
import id.my.githubuser.ui.SectionsPagerAdapter
import id.my.githubuser.ui.SettingPreferences
import id.my.githubuser.ui.viewmodel.ViewModelFactory
import id.my.githubuser.ui.datastore
import id.my.githubuser.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var detailUserViewModel: DetailUserViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var username: String
    private var isFavorite: Boolean = false
    private var isDarkMode: Boolean = false

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.datastore)
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(pref)
        )[MainViewModel::class.java]


        detailUserViewModel = obtainViewModel(this@DetailUserActivity)

        // get username data from MainActivity
        val username = intent.getStringExtra(EXTRA_USER)

        // find detail user through view model
        if (username != null) {
            detailUserViewModel.findDetailUser(username)
            detailUserViewModel.getFavoriteUserByUsername(username).observe(this) {
                isFavorite = it != null
            }
        }

        // observe live data detailUserViewModel
        detailUserViewModel.detailUser.observe(this) { detail ->
            setDetailUser(detail)
        }
        detailUserViewModel.isLoading.observe(this) {
            showLoading(it)
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

                R.id.share -> {
                    val textMessage =
                        getString(R.string.share_message, username)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, textMessage)
                        type = "text/plain"
                    }
                    if (sendIntent.resolveActivity(packageManager) != null) {
                        startActivity(sendIntent)
                    }
                    true
                }

                else -> false
            }
        }

        // setup the tabs with view pager
        val sectionPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionPagerAdapter

        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        // send username to sectionPagerAdapter
        if (username != null) sectionPagerAdapter.username = username
    }

    private fun setDetailUser(detail: DetailUserResponse) {
        Glide.with(this)
            .load(detail.avatarUrl)
            .into(binding.profileImage)
        binding.nameUser.text = detail.name
        binding.userName.text = detail.login
        binding.followers.text = getString(R.string.followers, detail.followers.toString())
        binding.followings.text = getString(R.string.followings, detail.following.toString())
        username = detail.login.toString()
        binding.fabFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                binding.fabFavorite.context,
                if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            )
        )

        // fab config
        binding.fabFavorite.setOnClickListener {
            binding.fabFavorite.isEnabled = false
            val favoriteUser = FavoriteUser(
                username = username,
                avatarUrl = detail.avatarUrl
            )

            binding.fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.fabFavorite.context,
                    if (!isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                )
            )

            lifecycleScope.launch {
                if (!isFavorite) {
                    detailUserViewModel.insert(favoriteUser)
                    Toast.makeText(this@DetailUserActivity, "Input favorite user success", Toast.LENGTH_SHORT).show()
                } else {
                    detailUserViewModel.delete(favoriteUser.username)
                    Toast.makeText(this@DetailUserActivity, "Delete from favorite user success", Toast.LENGTH_SHORT).show()
                }
            }
            binding.fabFavorite.isEnabled = true
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setIconMenu(isDarkMode: Boolean) {
        val modeMenu = binding.topAppBar.menu.findItem(R.id.mode)
        val shareMenu = binding.topAppBar.menu.findItem(R.id.share)

        if (isDarkMode) {
            modeMenu.icon = getDrawable(R.drawable.baseline_wb_sunny_24)
            shareMenu.icon = getDrawable(R.drawable.baseline_share_24_white)
            modeMenu.title = "Light Mode"
        } else {
            modeMenu.icon = getDrawable(R.drawable.baseline_dark_mode_24)
            shareMenu.icon = getDrawable(R.drawable.baseline_share_24)
            modeMenu.title = "Dark Mode"
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailUserViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[DetailUserViewModel::class.java]
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_followers_title,
            R.string.tab_following_title
        )
        const val EXTRA_USER = "extra_user"
    }

}