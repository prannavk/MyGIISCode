package com.giis.mobileappproto1.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.data.models.PersonSelectTagDTO
import com.giis.mobileappproto1.databinding.FragmentPeopleTagsBottomsheetBinding
import com.giis.mobileappproto1.databinding.PersonSelectTagLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// @AndroidEntryPoint
class PeopleTagsBottomSheet(
    private val tagsList: List<PersonSelectTagDTO>
) : BottomSheetDialogFragment() {

//    @Inject
//    lateinit var viewModel: PeopleTagsViewModel

    var onDismissListener: ((Pair<Int, PersonSelectTagDTO>?) -> Unit)? = null

    private var clickTagData: Pair<Int, PersonSelectTagDTO>? = null

    private lateinit var binding: FragmentPeopleTagsBottomsheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPeopleTagsBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
        // return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.peopleTagsRv.apply {
            adapter =
                PeopleTagsAdapter(this@PeopleTagsBottomSheet, this@PeopleTagsBottomSheet.tagsList)
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }

    }

    fun dataAndDismiss(thisDto: PersonSelectTagDTO?, thisPosition: Int) {
        Log.e(
            "Tags Issue",
            "Inside BottomSheetDialogFragment, pos: $thisPosition and ${thisDto?.pfpUrl ?: "Null value"}"
        )
        clickTagData = Pair(thisPosition, thisDto ?: PersonSelectTagDTO("Null value", "ERROR"))
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke(clickTagData)
    }


}

//class PeopleTagsViewModel @Inject constructor(private val repository: AppRepository) :
//    BaseViewModel() {
//    fun fetchAllSelectPersonTagsData(context: Context): List<PersonSelectTagDTO> {
//        return repository.fetchSelectTagsData(context) ?: mutableListOf<PersonSelectTagDTO>(
//            PersonSelectTagDTO("invalidUrl", "Unable to Show People")
//        ).toList()
//    }
//}

class PeopleTagsAdapter(
    private val fragmentReference: BottomSheetDialogFragment,
    private val peopleList: List<PersonSelectTagDTO>
) :
    RecyclerView.Adapter<PeopleTagsAdapter.TagViewHolder>() {

    inner class TagViewHolder(private val binding: PersonSelectTagLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), OnClickListener {

        init {
            binding.root.setOnClickListener(this::onClick)
        }

        private var thisDto: PersonSelectTagDTO? = null
        private var thisPosition: Int = -1

        fun bind(dto: PersonSelectTagDTO, position: Int) {
            binding.tagDTO = dto
            thisDto = dto
            thisPosition = position
        }

        override fun onClick(v: View?) {
            Log.e(
                "Tags Issue",
                "View OnClick -> $thisPosition's place -> ${thisDto?.pfpUrl ?: "Null value"}"
            )
            (fragmentReference as PeopleTagsBottomSheet).dataAndDismiss(thisDto, thisPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(fragmentReference.requireActivity())
        val binding = PersonSelectTagLayoutBinding.inflate(inflater, parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = peopleList.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(peopleList[position], position)
    }
}