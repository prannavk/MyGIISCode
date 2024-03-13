package com.giis.mobileappproto1.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.giis.mobileappproto1.ui.fragments.AllNotificationsFragment
import com.giis.mobileappproto1.ui.fragments.RequestsNotificationsFragment
import com.giis.mobileappproto1.ui.fragments.TaggedNotificationsFragment
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.databinding.ActivityNotificationsBinding

import com.giis.mobileappproto1.ui.viewmodels.SharedNotificationsViewModel
import com.giis.mobileappproto1.utils.globalTag
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsActivity : AppCompatActivity() {

    companion object {
        var allCount = 0
        var taggedCount = 0
        var requestsCount = 0
        // Commented following code since it may cause memory leak -> Instead I used an alternative approach
        // var allTabTextReference: TabLayout.Tab? = null
        // var taggedTabTextReference: TabLayout.Tab? = null
        // var reqTabTextReference: TabLayout.Tab? = null
    }

    @Inject
    lateinit var sharedViewModel: SharedNotificationsViewModel

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_notifications)
        binding = DataBindingUtil.setContentView(
            this@NotificationsActivity,
            R.layout.activity_notifications
        )

        setSupportActionBar(binding.notificationsHomeToolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        binding.tbIncl1.tbTitleTextView.text = getString(R.string.notification_title)
        binding.tbIncl1.tbHomeasupButton.setOnClickListener { finish() }
//        supportActionBar!!.setHomeAsUpIndicator(
//            ContextCompat.getDrawable(
//                this,
//                R.drawable.homeasup
//            )
//        )

        binding.notifFragsVp2.adapter =
            NotificationsViewPagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.notifTabs, binding.notifFragsVp2, true) { tab, position ->
            when (position) {
                0 -> tab.text = "All ($allCount)"
                1 -> tab.text = "Tagged ($taggedCount)"
                2 -> tab.text = "Requests ($requestsCount)"
            }
        }.attach()

    }

    private fun updateTabText(tabIndex: Int, newText: String) {
        val tab = binding.notifTabs.getTabAt(tabIndex)
        tab?.text = newText
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    inner class NotificationsViewPagerAdapter(
        supportFragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(supportFragmentManager, lifecycle) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AllNotificationsFragment().apply { setTabUpdateListener(this@NotificationsActivity::updateTabText) }
                1 -> TaggedNotificationsFragment().apply { setTabUpdateListener(this@NotificationsActivity::updateTabText) }
                2 -> RequestsNotificationsFragment().apply { setTabUpdateListener(this@NotificationsActivity::updateTabText) }
                else -> Fragment().also { Log.e(globalTag, "Notifications ViewPager Error") }
            }
        }

    }

}