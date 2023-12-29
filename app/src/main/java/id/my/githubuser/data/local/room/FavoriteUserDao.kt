package id.my.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.my.githubuser.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: FavoriteUser)

    @Query("DELETE FROM user_data WHERE username=:username")
    suspend fun delete(username: String)

    @Query("SELECT * from user_data")
    fun getAllNotes(): LiveData<List<FavoriteUser>>

    @Query("SELECT * FROM user_data WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser>
}