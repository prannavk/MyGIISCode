package com.giis.mobileappproto1.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.giis.mobileappproto1.GIISSessionTokenDataManager
import com.giis.mobileappproto1.ui.adapters.PostAdapter
import com.giis.mobileappproto1.ui.adapters.UpcomingClassesAdapter
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.ui.viewmodels.CommonViewModel1
import com.giis.mobileappproto1.ui.viewmodels.HomeViewModel
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.LabelType
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.databinding.FragmentHomeBinding
import com.giis.mobileappproto1.ui.activities.CreatePostActivity
import com.giis.mobileappproto1.ui.activities.MainActivity
import com.giis.mobileappproto1.ui.activities.MainActivity.MainAct.capturedBitmap
import com.giis.mobileappproto1.ui.adapters.ENoticeBoardAdapter
import com.giis.mobileappproto1.ui.viewmodels.CreatePostViewModel
import com.giis.mobileappproto1.utils.Commons
import com.giis.mobileappproto1.utils.createFailResultCode
import com.giis.mobileappproto1.utils.createPassResultCode
import com.giis.mobileappproto1.utils.globalTag
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object HomeCompanion {

        fun getUnderlinedText(context: Context?): SpannableString {
            val text = context?.getString(R.string.view_all) ?: "View all"
            val spannableString = SpannableString(text).apply {
                setSpan(UnderlineSpan(), 0, text.length, 0)
            }
            return spannableString
        }

    }

    @Inject
    lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var postCreationViewModel: CreatePostViewModel

    @Inject
    lateinit var viewModelCommon: CommonViewModel1

    private lateinit var binding: FragmentHomeBinding

    private var createdPostObject: FeedPostDTO? = null

    private lateinit var feedAdapter: PostAdapter

    //Lambda for -> ActivityResultCallBack<ActivityResult>()'s onActivityResult Impl
    private val homeCreatePostActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult != null && activityResult.resultCode == createPassResultCode) {
                activityResult.data?.let { intent ->
                    createdPostObject =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(
                            "newFeedPost",
                            FeedPostDTO::class.java
                        ) else intent.getParcelableExtra("newFeedPost") as FeedPostDTO?
                }
                createdPostObject?.let {
                    Log.e(
                        "ARL",
                        "FeedPostDTO received: Success: $it"
                    )
                    Log.e("RV Issue", "RV size: ${feedAdapter.itemCount} + 1 = ${feedAdapter.itemCount + 1}")
                    Log.e("RV Issue", "viewModelFeedPostsList: ${viewModelCommon.myFeedPostsList.size}")
                    feedAdapter.addDtoAndUpdateView(it)
                    viewModelCommon.myFeedPostsList.add(it)
                    Log.e("RV Issue", "After -> RV size: ${feedAdapter.itemCount} + 1 = ${feedAdapter.itemCount + 1}")
                    Log.e("RV Issue", "After -> viewModelFeedPostsList: ${viewModelCommon.myFeedPostsList.size}")
                } ?: kotlin.run { Log.e("ARL", "FeedPostDTO couldn't be fetched") }
            } else if (activityResult != null && activityResult.resultCode == createFailResultCode) {
                Log.e("ARL", "New FeedPost not received -> Back Pressed")
            } else {
                Log.e("ARL", "ActivityResult is null or Create Post activity has been cancelled")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feedAdapter = PostAdapter(
            viewModelCommon.myFeedPostsList.toList(),
            this@HomeFragment,
            requireActivity()
        )
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting Up UnderLines to both the 'View All' texts
        binding.viewAllENB.text = getUnderlinedText(context)
        binding.viewAll2.text = getUnderlinedText(context)
        binding.postsEndViewAll.text = getUnderlinedText(context)

        val sessionManager = GIISSessionTokenDataManager(requireActivity().applicationContext)
        sessionManager.getVerifiedSessionDTO()?.let {
            it.nameOfPerson?.let { name ->
                binding.welcomeTitleTv.text = "Welcome Back, $name"
            } ?: run { Log.e(globalTag, "--------    Name not set since Null") }
        } ?: run { Log.e(globalTag, "----------   Name not set since dto is null") }

        // Setting Up the E Notice Board ViewPager2
        binding.noticeCardRecyclerView.apply {
            adapter =
                ENoticeBoardAdapter(
                    viewModelCommon.obtainNoticeData(requireActivity()),
                    requireActivity()
                )
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        }
        binding.noticeCardIndicator.attachToRecyclerView(binding.noticeCardRecyclerView)

        // Setting Up the Upcoming Classes Horizontal RecyclerView
        binding.upcomingClassesRv.apply {
            adapter =
                UpcomingClassesAdapter(
                    viewModelCommon.provideUpcomingClassesHardCodedData(),
                    requireActivity()
                )
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.postsRV.apply {
            adapter = this@HomeFragment.feedAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }


        binding.homeFragmentNsw.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (!binding.postInitEditText.isEnabled) {
                binding.postInitEditText.isEnabled = true
            }
            if (binding.plsMsgTv.visibility == View.VISIBLE) {
                binding.plsMsgTv.visibility = View.GONE
            }
            val viewAnim = binding.animationView1
            if (viewAnim.isAnimating) {
                viewAnim.cancelAnimation()
                viewAnim.visibility = View.GONE
                viewAnim.animation = null
            }
            if (scrollY < oldScrollY) {
                // scrollEditToggle = true
            }

            MainActivity.mainConstLayoutReference?.let {
                val rootViewRef = v.rootView
                MainActivity.capturedBitmap = Bitmap.createBitmap(
                    MainActivity.topViewWidth,
                    MainActivity.topViewHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(MainActivity.capturedBitmap!!)
                canvas.translate(
                    -it.left.toFloat(),
                    -(it.top.toFloat())
                )
                try {
                    rootViewRef.draw(canvas)
                } catch (ex: Exception) {
                    Log.e(globalTag, "------- skipped drawing into canvas - HomeFragment Capture")
                }
                val finalBitMap =
                    MainActivity.applyGaussianBlur(requireActivity(), capturedBitmap!!, 17f)
                val finalBitMapDrawable = BitmapDrawable(resources, finalBitMap)
                it.setBackgroundDrawable(finalBitMapDrawable)
            } ?: kotlin.run {
                Log.e(
                    "REFERENCE ERROR",
                    "BubbleBar ConstraintLayout reference obtained is null - HomeFragment"
                )
            }

        }

        binding.postInitEditText.setOnClickListener {
            try {
                homeCreatePostActivityLauncher.launch(
                    Intent(
                        requireActivity(),
                        CreatePostActivity::class.java
                    )
                )
            } catch (npe: NullPointerException) {
                Log.e(globalTag, "----- CreatePostActivity ARL NullPointerException")
            } catch (e: java.lang.Exception) {
                Log.e(globalTag, "----- CreatePostActivity ARL Exception")
            }
        }


    }

    override fun onPause() {
        super.onPause()
        if (binding.animationView1.animation != null) {
            binding.animationView1.pauseAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.animationView1.animation != null && !binding.animationView1.isAnimating) {
            binding.animationView1.resumeAnimation()
        }
    }


}

