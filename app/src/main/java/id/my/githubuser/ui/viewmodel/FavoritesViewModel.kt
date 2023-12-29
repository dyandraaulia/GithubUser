package id.my.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import id.my.githubuser.data.FavoriteUserRepository

class FavoritesViewModel(private val repository: FavoriteUserRepository) : ViewModel() {
    fun getAllFavorites() = repository.getAllFavorites()
}