package id.my.githubuser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class FavoriteUser(
    @PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    val id: Int = 0,

    @field:ColumnInfo(name = "username")
    var username: String = "",

    @field:ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null,
)