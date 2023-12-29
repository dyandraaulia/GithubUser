package id.my.githubuser.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.my.githubuser.data.remote.response.ItemsItem
import id.my.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel : ViewModel() {
    private val _follow = MutableLiveData<List<ItemsItem>>()
    val follow: LiveData<List<ItemsItem>> = _follow

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun findFollow(username: String, position: Int) {
        _isLoading.value = true
        var client: Call<List<ItemsItem>> = if (position == 1) {
            ApiConfig.getApiService().getDetailFollowers(username)
        } else {
            ApiConfig.getApiService().getDetailFollowing(username)
        }

        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _follow.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object {
        private const val TAG = "FollowViewModel"
    }
}