package com.giis.mobileappproto1.ui.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.ui.bottomsheets.MeatBallBottomSheetFragment
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.DimensionsBindingUtil
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.databinding.ActualFeedpostLayoutBinding
import com.giis.mobileappproto1.utils.globalTag
import java.util.Calendar
import java.util.Date

class PostAdapter(
    private var homePostList: List<FeedPostDTO>,
    private val parentFragment: Fragment,
    private val parentActivity: FragmentActivity
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    companion object {
        fun compareDates(date1: Date, date2: Date): Boolean {
            val calendar1 = Calendar.getInstance().apply {
                time = date1
                clear(Calendar.HOUR_OF_DAY)
                clear(Calendar.MINUTE)
                clear(Calendar.SECOND)
                clear(Calendar.MILLISECOND)
            }

            val calendar2 = Calendar.getInstance().apply {
                time = date2
                clear(Calendar.HOUR_OF_DAY)
                clear(Calendar.MINUTE)
                clear(Calendar.SECOND)
                clear(Calendar.MILLISECOND)
            }

            return calendar1 == calendar2
        }
    }

    inner class PostViewHolder(private val binding: ActualFeedpostLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(feedPostDTO: FeedPostDTO, position: Int) {
            binding.sizeUtil = provideDimensionsUtil()
            binding.bindPost = feedPostDTO
            binding.postSharesCounter.text = "${feedPostDTO.postShareCount}K"

            if (feedPostDTO.postTagsBoolean && feedPostDTO.postTagsList != null) {
                val shortTagText =
                    "${feedPostDTO.postTagsList[0]} and ${feedPostDTO.postTagsList.size - 1} others"
                binding.repTagTextView.text = shortTagText
            } else {
                binding.tagsLinearlayout.visibility = View.GONE
            }

            // binding.posterPostedDetails.text
            val isDateToday = compareDates(feedPostDTO.postCreationDateTime, Date())
            if (isDateToday) {
                val creatorString =
                    "${feedPostDTO.postCreatorDetails}, ${feedPostDTO.postCreationDateTime.hours}:${feedPostDTO.postCreationDateTime.minutes}"
                binding.posterPostedDetails.text = creatorString
                binding.agedPostTextView.visibility = View.INVISIBLE
            } else {
                binding.posterPostedDetails.text = feedPostDTO.postCreatorDetails
                binding.agedPostTextView.text =
                    "${feedPostDTO.postCreationDateTime.day + 1}/${feedPostDTO.postCreationDateTime.month + 1}/20${feedPostDTO.postCreationDateTime.year - 100}"
            }

            binding.meatballPostMenu.setOnClickListener {
                val showBottomSheet = MeatBallBottomSheetFragment()
                showBottomSheet.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.CustomBottomSheetDialogTheme
                )
                val bundle = Bundle()
                val callerString = parentFragment.javaClass.canonicalName?.let { it1 ->
                    Log.e(
                        globalTag,
                        it1
                    ); it1
                }
                bundle.putString("caller", callerString)
                bundle.putParcelable("postCalledOn", feedPostDTO)
                showBottomSheet.arguments = bundle
                showBottomSheet.show(parentActivity.supportFragmentManager, "")
            }

        }

    }

    private fun provideDimensionsUtil(): DimensionsBindingUtil {
        return DimensionsBindingUtil(
            unitRectangleHeight = parentActivity.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._93sdp),
            unitRectangleWidth = parentActivity.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._137sdp),
            unitSquareSide = parentActivity.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._91sdp),
            maxRectangleHeight = parentActivity.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._186sdp),
            maxRectangleWidth = parentActivity.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._274sdp)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parentActivity)
        val binding = ActualFeedpostLayoutBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = homePostList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(homePostList[position], position)
    }

    fun addDtoAndUpdateView(newFeedPostDTO: FeedPostDTO) {
        val repList = mutableListOf<FeedPostDTO>()
        repList.addAll(this.homePostList)
        repList.add(newFeedPostDTO)
        this.homePostList = repList.toList()
        notifyItemInserted(this.homePostList.lastIndex)
    }
}