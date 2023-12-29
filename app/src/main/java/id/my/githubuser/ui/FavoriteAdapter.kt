package id.my.githubuser.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.my.githubuser.data.local.entity.FavoriteUser
import id.my.githubuser.databinding.ItemResultBinding

class FavoriteAdapter :
    ListAdapter<FavoriteUser, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class FavoriteViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: FavoriteUser, clickListener: OnItemClickCallback) {
            binding.username.text = result.username
            Glide.with(binding.resultItem.context)
                .load(result.avatarUrl)
                .into(binding.imgItemPhoto)
            binding.resultItem.setOnClickListener {
                clickListener.onItemClicked(result)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result, onItemClickCallback)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: FavoriteUser)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteUser>() {
            override fun areItemsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
                return oldItem == newItem
            }
        }
    }
}