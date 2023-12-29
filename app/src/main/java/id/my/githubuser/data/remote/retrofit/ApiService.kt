package id.my.githubuser.data.remote.retrofit

import id.my.githubuser.data.remote.response.DetailUserResponse
import id.my.githubuser.data.remote.response.GithubResponse
import id.my.githubuser.data.remote.response.ItemsItem
import retrofit2.Call
import retrofit2.http.*
import id.my.githubuser.BuildConfig

interface ApiService {
    @GET("search/users")
    @Headers("Authorization: token ${BuildConfig.KEY}")
    fun getListUsers(
        @Query("q") username: String
    ): Call<GithubResponse>

    @GET("users/{username}")
    @Headers("Authorization: token ${BuildConfig.KEY}")
    fun getDetailUser(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token ${BuildConfig.KEY}")
    fun getDetailFollowers(
        @Path("username") username: String
    ): Call<List<ItemsItem>>

    @GET("users/{username}/following")
    @Headers("Authorization: token ${BuildConfig.KEY}")
    fun getDetailFollowing(
        @Path("username") username: String
    ): Call<List<ItemsItem>>
}