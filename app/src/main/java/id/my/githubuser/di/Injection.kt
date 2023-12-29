package id.my.githubuser.di

import android.content.Context
import id.my.githubuser.data.FavoriteUserRepository
import id.my.githubuser.data.local.database.FavoriteUserDatabase

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val database = FavoriteUserDatabase.getDatabase(context)
        val usersDao = database.favoriteUserDao()
        return FavoriteUserRepository.getInstance(usersDao)
    }
}