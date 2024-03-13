package com.giis.mobileappproto1.ui.bottomsheets

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.databinding.FragmentMeatBallBottomSheetBinding
import com.giis.mobileappproto1.ui.viewmodels.MeatBallBottomSheetViewModel
import com.giis.mobileappproto1.utils.globalTag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeatBallBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {

    }

    @Inject
    lateinit var viewModel: MeatBallBottomSheetViewModel

    private lateinit var binding: FragmentMeatBallBottomSheetBinding
    private var fromClassName: String = ""
    private var savedPostsFlag: Boolean = false
    private var postCalledOn: FeedPostDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            this.fromClassName = it.getString("caller")!!
            this.postCalledOn =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) it.getParcelable(
                    "postCalledOn",
                    FeedPostDTO::class.java
                )!! else it.getParcelable("postCalledOn")!!
        } ?: kotlin.run {
            Log.e(
                globalTag, "Data not received"
            )
        }
        binding = FragmentMeatBallBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_meat_ball_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.fromClassName == "com.giis.mobileappproto1.ui.fragments.MySavedPostsSubFragment") {
            binding.savedposts.visibility = View.VISIBLE
            this.savedPostsFlag = true
            if (binding.notsavedposts.visibility == View.VISIBLE) {
                binding.notsavedposts.visibility = View.GONE
            }
        } else {
            binding.notsavedposts.visibility = View.VISIBLE
            if (binding.savedposts.visibility == View.VISIBLE) {
                binding.notsavedposts.visibility = View.GONE
            }
        }

        if (this.savedPostsFlag) {
            binding.unSavepostOption.setOnClickListener {
                // Fire Delete Operation from DB
                Log.e(globalTag, "Delete -> FeedPostDTO of ${postCalledOn!!.postCreatorName}")
                viewModel.removeThisPostFromDb(postCalledOn!!)
                dismiss()
            }
        } else {
            binding.savepostOption.setOnClickListener {
                // Fire Insert Operation into DB
                Log.e(globalTag, "Insert -> FeedPostDTO of ${postCalledOn!!.postCreatorName}")
                viewModel.saveThisPostInDb(postCalledOn!!)
                dismiss()
            }
            binding.reportOption.setOnClickListener {
                Toast.makeText(
                    context,
                    "Report functionality under development",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}