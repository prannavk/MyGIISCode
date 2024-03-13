package com.giis.mobileappproto1.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.databinding.FragmentMyFeedSubBinding
import com.giis.mobileappproto1.ui.activities.CreatePostActivity
import com.giis.mobileappproto1.ui.activities.MainActivity
import com.giis.mobileappproto1.ui.adapters.PostAdapter
import com.giis.mobileappproto1.ui.viewmodels.CommonViewModel1
import com.giis.mobileappproto1.ui.viewmodels.CreatePostViewModel
import com.giis.mobileappproto1.utils.globalTag
import javax.inject.Inject

class MyFeedSubFragment(private val parentViewModel: CommonViewModel1) : Fragment() {

    companion object {
        var postCreationFlag = false
    }

    private lateinit var binding: FragmentMyFeedSubBinding

    @Inject
    lateinit var postCreationViewModel: CreatePostViewModel

    private var createdPostObject: FeedPostDTO? = null

    private lateinit var feedAdapter: PostAdapter

    //Lambda for -> ActivityResultCallBack<ActivityResult>()'s onActivityResult Impl
    private val feedCreatePostActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            // Logic Here:
            // get the new created FeedPostDTO Object from the CreatePostActivity
            // write the object into the JSON Array having the FeedPostDTO objects -> which is the data source of the postsFeedSubRv
            // notifyDataSetChanged for postsFeedSubRv, so that the update json data is read
            // Also:
            // (Here or elsewhere) Write multipart request live data response observer logic which is:
            // Update url of the specific DTO object which was just added
            if (activityResult != null && activityResult.resultCode == 11) {
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
                        "FeedPostDTO received: Success: ${it.postText}"
                    )
                    Log.e("RV Issue", "RV size: ${feedAdapter.itemCount} + 1 = ${feedAdapter.itemCount + 1}")
                    Log.e("RV Issue", "viewModelFeedPostsList: ${parentViewModel.myFeedPostsList.size}")
                    feedAdapter.addDtoAndUpdateView(it)
                    parentViewModel.myFeedPostsList.add(it)
                    Log.e("RV Issue", "After -> RV size: ${feedAdapter.itemCount} + 1 = ${feedAdapter.itemCount + 1}")
                    Log.e("RV Issue", "After -> viewModelFeedPostsList: ${parentViewModel.myFeedPostsList.size}")
                } ?: kotlin.run { Log.e("ARL", "FeedPostDTO couldn't be fetched") }
            } else if (activityResult != null && activityResult.resultCode == 10) {
                Log.e("ARL", "New FeedPost not received -> Back Pressed")
            } else {
                Log.e("ARL", "ActivityResult is null or Create Post activity has been cancelled")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.feedAdapter = PostAdapter(
            parentViewModel.myFeedPostsList.toList(),
            this@MyFeedSubFragment,
            requireActivity()
        )
        binding = FragmentMyFeedSubBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_my_feed_sub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postsFeedSubRv.apply {
            adapter = this@MyFeedSubFragment.feedAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }

        binding.myFeedSubMyFeedNsw.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            MainActivity.mainConstLayoutReference?.let {
                val rootView = v.rootView
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
                    rootView.draw(canvas)
                } catch (ex: Exception) {
                    Log.e(
                        globalTag,
                        "------- skipped drawing into canvas - MyFeedSubFragment Capture"
                    )
                }
                val finalBitMap =
                    MainActivity.applyGaussianBlur(
                        requireActivity(),
                        MainActivity.capturedBitmap!!,
                        17f
                    )
                val finalBitMapDrawable = BitmapDrawable(resources, finalBitMap)
                it.setBackgroundDrawable(finalBitMapDrawable)
            } ?: kotlin.run {
                Log.e(
                    "REFERENCE ERROR",
                    "BubbleBar ConstraintLayout reference obtained is null - MyFeedSubFragment"
                )
            }
        }

        binding.postInitEditTextMfs.setOnClickListener {
            feedCreatePostActivityLauncher.launch(
                Intent(
                    requireActivity(),
                    CreatePostActivity::class.java
                )
            )
        }

        // Observer Logics of Multipart response -> Possible place

    }

}