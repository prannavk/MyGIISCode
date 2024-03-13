package com.giis.mobileappproto1.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.data.models.LabelType
import com.giis.mobileappproto1.databinding.FragmentMySavedPostsSubBinding
import com.giis.mobileappproto1.ui.activities.MainActivity
import com.giis.mobileappproto1.ui.adapters.PostAdapter
import com.giis.mobileappproto1.ui.viewmodels.CommonViewModel1
import com.giis.mobileappproto1.utils.globalTag
import java.util.Date

class MySavedPostsSubFragment(private val parentViewModel: CommonViewModel1) : Fragment() {

    private lateinit var binding: FragmentMySavedPostsSubBinding

    private var dataPersistedFirstTime: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySavedPostsSubBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_my_saved_posts_sub, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postsFeedSubRvSaved.apply {
            adapter = PostAdapter(
                providePlaceHolderHardcodedDataIfNoSavedPosts(), // Need to replace this with data from room when LiveData is observed
                this@MySavedPostsSubFragment, requireActivity()
            )
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }

        // Keep observer and when data is obtained, update the adapter
        this.parentViewModel.savedPostsData.observe(viewLifecycleOwner) {
            Log.e(globalTag, "Posts Data Has been Retrieved from SQLite")
            binding.postsFeedSubRvSaved.adapter =
                PostAdapter(it, this@MySavedPostsSubFragment, requireActivity())
//            if (dataPersistedFirstTime) {
//                dataPersistedFirstTime = false
//            } else {
//                binding.postsFeedSubRvSaved.adapter?.notifyDataSetChanged()
//            }
        }

        binding.postsFeedSubRvSaved.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
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
                        "------- skipped drawing into canvas - MySavedPostsSubFragmentsCapture"
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


    }

    companion object {
        fun providePlaceHolderHardcodedDataIfNoSavedPosts(): List<FeedPostDTO> {
            return listOf(
                FeedPostDTO(
                    postId = 0L,
                    postCreatorName = "Sample Person",
                    // postCreationDetails = "6B Grade, Noida, 9:00AM",
                    postCreatorDetails = "Sample Grade, Sample Campus",
                    postCreationDateTime = Date(),
                    postLabel = LabelType.ONLY_TEXT,
                    postImageStatus = false,
                    postText = "Please check Data",
                    postImageList = null,
                    postAcknowledgeCount = 123,
                    postCommentsCount = 232,
                    postShareCount = 12,
                    postTagsBoolean = false,
                    postTagsList = null,
                    postPollData = null,
                    postPollStatus = false,
                    postScheduledDateTime = null,
                    postScheduledFlag = false,
                    postVideoStatus = false,
                    postVideoUrlString = null,
                    postCreationErrorFlag = false,
                    postCommentsOn = true
                )
            )
        }
    }
}