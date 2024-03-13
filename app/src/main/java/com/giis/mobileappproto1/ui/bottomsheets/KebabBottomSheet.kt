package com.giis.mobileappproto1.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.giis.mobileappproto1.databinding.FragmentKebabBottomSheetBinding
import com.giis.mobileappproto1.ui.viewmodels.KebabViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KebabBottomSheet : BottomSheetDialogFragment() {

    companion object {

    }

    @Inject
    lateinit var viewModel: KebabViewModel

    private lateinit var binding: FragmentKebabBottomSheetBinding
    private var clickedOnNotId: Int = -1
    private var obtainedReadFlag: Boolean = false

    var updateNotifUIFunReference: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            this.clickedOnNotId = it.getInt("selectedNotId")
            this.obtainedReadFlag = it.getBoolean("selectedReadFlag")
        }
        binding = FragmentKebabBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
        // return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.markUnReadOption.setOnClickListener {
            if (this.clickedOnNotId != -1) {
                if (this.obtainedReadFlag) {
                    Toast.makeText(
                        requireActivity(),
                        "Notification is already read, hence not marked",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // update json
                    viewModel.updateAsRead(requireActivity(), this.clickedOnNotId, true)
                    // trigger ui change
                    this.updateNotifUIFunReference?.invoke()
                    Toast.makeText(
                        requireActivity(),
                        "Notification marked as read",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dismiss()
        }
    }

    fun setUpdateNotifUIListener(listener: (() -> Unit)?) {
        this.updateNotifUIFunReference = listener
    }

}