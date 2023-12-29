package id.my.githubuser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.githubuser.data.remote.response.ItemsItem
import id.my.githubuser.databinding.FragmentFollowBinding
import id.my.githubuser.ui.viewmodel.FollowViewModel
import id.my.githubuser.ui.ResultAdapter
import id.my.githubuser.ui.activity.DetailUserActivity

class FollowFragment : Fragment() {
    private var position = 0
    var username: String = ""
    private lateinit var followViewModel: FollowViewModel
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        followViewModel = ViewModelProvider(this)[FollowViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting the recycler view
        val layoutManager = LinearLayoutManager(requireActivity())
        binding?.rvFollow?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding?.rvFollow?.addItemDecoration(itemDecoration)

        // find followers/following
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME).toString()
        }
        followViewModel.findFollow(username, position)

        // observe live data
        followViewModel.follow.observe(viewLifecycleOwner) { follow ->
            setFollowData(follow)
        }
        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setFollowData(follows: List<ItemsItem>) {
        val adapter = ResultAdapter()
        adapter.submitList(follows)
        binding?.rvFollow?.adapter = adapter

        adapter.setOnItemClickCallback(object : ResultAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                val profileData = data.login

                // show detail user profile
                val intent = Intent(requireActivity(), DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, profileData)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"
    }
}