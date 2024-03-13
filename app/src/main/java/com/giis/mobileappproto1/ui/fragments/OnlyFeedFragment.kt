package com.giis.mobileappproto1.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.giis.mobileappproto1.databinding.FragmentOnlyFeedBinding
import com.giis.mobileappproto1.ui.viewmodels.CommonViewModel1
import com.giis.mobileappproto1.ui.viewmodels.OnlyFeedViewModel
import com.giis.mobileappproto1.utils.globalTag
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnlyFeedFragment : Fragment() {
    companion object {

    }

    @Inject
    lateinit var viewModel: OnlyFeedViewModel

    @Inject
    lateinit var viewModelShared: CommonViewModel1

    private lateinit var binding: FragmentOnlyFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlyFeedBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager2FeedTs.adapter =
            MyFeedViewPagerAdapter(fragmentManager = childFragmentManager, lifecycle = lifecycle)
        TabLayoutMediator(
            binding.tabLayoutFeedTs,
            binding.viewPager2FeedTs,
            true
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "My Feed"
                1 -> tab.text = "Saved Posts"
            }
        }.attach()



    }

    inner class MyFeedViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    MyFeedSubFragment(viewModelShared)
                }

                1 -> {
                    MySavedPostsSubFragment(viewModelShared)
                }

                else -> {
                    Log.e(globalTag, "Fragment ViewPager Adapter Error")
                    Fragment()
                }
            }
        }
    }


}