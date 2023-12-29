package id.my.githubuser.data

import androidx.lifecycle.LiveData
import id.my.githubuser.data.local.room.FavoriteUserDao
import id.my.githubuser.data.local.entity.FavoriteUser

class FavoriteUserRepository private constructor(
    private val mFavoriteUserDao: FavoriteUserDao
) {

    fun getAllFavorites(): LiveData<List<FavoriteUser>> = mFavoriteUserDao.getAllNotes()

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> =
        mFavoriteUserDao.getFavoriteUserByUsername(username)

    suspend fun insertFavorite(user: FavoriteUser) = mFavoriteUserDao.insert(user)

    suspend fun deleteFavorite(username: String) = mFavoriteUserDao.delete(username)

    companion object {
        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            favoriteUserDao: FavoriteUserDao,
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(favoriteUserDao)
            }.also { instance = it }
    }
}