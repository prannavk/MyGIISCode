package com.giis.mobileappproto1.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.NDateCategory
import com.giis.mobileappproto1.data.models.NotificationDTO
import com.giis.mobileappproto1.databinding.FragmentAllNotifsBinding
import com.giis.mobileappproto1.databinding.FragmentRequestsNotifsBinding
import com.giis.mobileappproto1.databinding.FragmentTaggedNotifsBinding
import com.giis.mobileappproto1.databinding.NotificationLayoutBinding
import com.giis.mobileappproto1.databinding.NotificationgroupLayoutBinding
import com.giis.mobileappproto1.ui.bottomsheets.KebabBottomSheet
import com.giis.mobileappproto1.ui.viewmodels.SharedNotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.PriorityQueue
import javax.inject.Inject

@AndroidEntryPoint
open class NotificationFragments : Fragment() {
    var updateTabText: ((Int, String) -> Unit)? = null
    fun setTabUpdateListener(listener: (Int, String) -> Unit) {
        updateTabText = listener
    }
}

@AndroidEntryPoint
class AllNotificationsFragment() : NotificationFragments() {

    @Inject
    lateinit var sharedViewModel: SharedNotificationsViewModel

    private lateinit var binding: FragmentAllNotifsBinding
    private val dataRequestKey = "ALL"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllNotifsBinding.inflate(inflater, container, false)
        return binding.root
        // return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationsGroupsData: PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>>? =
            sharedViewModel.requestNotificationData(requireActivity(), this.dataRequestKey)
        var notifsCount = 0
        notificationsGroupsData?.let {
            notificationsGroupsData.forEach { (k, v) -> notifsCount += v?.size ?: 0 }
            updateTabText?.invoke(0, "All ($notifsCount)")
            binding.notifsMainGroupRv.apply {
                adapter = NotificationsGroupRecyclerViewAdapter(
                    context = requireActivity(),
                    notificationsGroupsMapList = it.toList() // groupsInOrder.toList() // notificationGroups.toList()
                )
                layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        } ?: kotlin.run { Log.e("NotificationDataRequest", "null value received") }

    }

    override fun onStart() {
        super.onStart()
    }
}

@AndroidEntryPoint
class TaggedNotificationsFragment() : NotificationFragments() {

    @Inject
    lateinit var sharedViewModel: SharedNotificationsViewModel

    private lateinit var binding: FragmentTaggedNotifsBinding
    private val dataRequestKey = "TAG"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaggedNotifsBinding.inflate(inflater, container, false)
        return binding.root
        // return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationsGroupsData: PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>>? =
            sharedViewModel.requestNotificationData(requireActivity(), this.dataRequestKey)
        var notifsCount = 0
        notificationsGroupsData?.let {
            notificationsGroupsData.forEach { (k, v) -> notifsCount += v?.size ?: 0 }
            updateTabText?.invoke(1, "Tagged ($notifsCount)")
            binding.notifsMainGroupRvTag.apply {
                adapter = NotificationsGroupRecyclerViewAdapter(
                    context = requireActivity(),
                    notificationsGroupsMapList = it.toList() // groupsInOrder.toList() // notificationGroups.toList()
                )
                layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        } ?: kotlin.run { Log.e("NotificationDataRequest", "null value received") }

    }

    override fun onStart() {
        super.onStart()
    }
}

@AndroidEntryPoint
class RequestsNotificationsFragment() : NotificationFragments() {

    @Inject
    lateinit var sharedViewModel: SharedNotificationsViewModel

    private lateinit var binding: FragmentRequestsNotifsBinding
    private val dataRequestKey = "REQ"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestsNotifsBinding.inflate(inflater, container, false)
        return binding.root
        // return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationsGroupsData: PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>>? =
            sharedViewModel.requestNotificationData(requireActivity(), this.dataRequestKey)
        var notifsCount = 0
        notificationsGroupsData?.let {
            binding.notifsMainGroupRvReq.apply {
                notificationsGroupsData.forEach { (k, v) -> notifsCount += v?.size ?: 0 }
                updateTabText?.invoke(2, "Requests ($notifsCount)")
                adapter = NotificationsGroupRecyclerViewAdapter(
                    context = requireActivity(),
                    notificationsGroupsMapList = it.toList() // groupsInOrder.toList() // notificationGroups.toList()
                )
                layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        } ?: kotlin.run { Log.e("NotificationDataRequest", "null value received") }
    }

    override fun onStart() {
        super.onStart()
    }
}

class NotificationsGroupRecyclerViewAdapter(
    private val context: Context,
    private val notificationsGroupsMapList: List<Pair<NDateCategory, MutableList<NotificationDTO>?>>
) : RecyclerView.Adapter<NotificationsGroupRecyclerViewAdapter.NotificationGroupViewHolder>() {

    inner class NotificationGroupViewHolder(private val binding: NotificationgroupLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dtoList: MutableList<NotificationDTO>?, notifHeader: NDateCategory) {
            if (dtoList != null && dtoList.size > 0) {
                binding.groupTitle = notifHeader.name
                binding.groupNotifsRv.apply {
                    adapter = NotificationsRecyclerViewAdapter(context, dtoList.toList())
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationGroupViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = NotificationgroupLayoutBinding.inflate(inflater, parent, false)
        return NotificationGroupViewHolder(binding)
    }

    override fun getItemCount(): Int = this.notificationsGroupsMapList.size

    override fun onBindViewHolder(holder: NotificationGroupViewHolder, position: Int) {
        val entry = this.notificationsGroupsMapList[position]
        holder.bind(dtoList = entry.second, notifHeader = entry.first)
    }
}

class NotificationsRecyclerViewAdapter(
    val context: Context,
    val notifsList: List<NotificationDTO>
) : RecyclerView.Adapter<NotificationsRecyclerViewAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: NotificationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var fromParent = (context as FragmentActivity)

        fun bind(dto: NotificationDTO) {
            binding.notif = dto
            if (dto.isNotificationRequest) {
                binding.reqBtnSet.visibility = View.VISIBLE
                binding.reqBtnAccept.setOnClickListener {
                    Toast.makeText(
                        context,
                        "Mock Request Accepted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.reqBtnReject.setOnClickListener {
                    Toast.makeText(
                        context,
                        "Mock Request Rejected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.reqBtnSet.visibility = View.GONE
            }

            if (dto.isRead) {
                updateNotificationUI()
            }

            binding.kebabOptions.setOnClickListener {
                val showBottomSheet = KebabBottomSheet().apply { setUpdateNotifUIListener(this@NotificationViewHolder::updateNotificationUI) }
                showBottomSheet.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.CustomBottomSheetDialogTheme
                )
                val bundle = Bundle()
                bundle.putInt("selectedNotId", dto.notId)
                bundle.putBoolean("selectedReadFlag", dto.isRead)
                showBottomSheet.arguments = bundle
                val fragmentManager = fromParent.supportFragmentManager
                showBottomSheet.show(fragmentManager, "")
            }

        }

        private fun updateNotificationUI(): Unit {
            binding.unreadDotView.visibility = View.INVISIBLE
            binding.notificationMainConstLayout.backgroundTintList =
                AppCompatResources.getColorStateList(context, R.color.default_bg)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = NotificationLayoutBinding.inflate(inflater, parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = notifsList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifsList[position])
    }
}