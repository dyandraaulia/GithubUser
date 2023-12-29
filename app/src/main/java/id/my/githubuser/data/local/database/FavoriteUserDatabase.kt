package id.my.githubuser.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import id.my.githubuser.data.local.entity.FavoriteUser
import id.my.githubuser.data.local.room.FavoriteUserDao

@Database(
    entities = [FavoriteUser::class],
    version = 1
)
abstract class FavoriteUserDatabase : RoomDatabase() {
    abstract fun favoriteUserDao(): FavoriteUserDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteUserDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FavoriteUserDatabase {
            if (INSTANCE == null) {
                synchronized(FavoriteUserDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        FavoriteUserDatabase::class.java, "favorite_user"
                    )
                        .build()
                }
            }
            return INSTANCE as FavoriteUserDatabase
        }
    }
}