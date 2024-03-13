package com.giis.mobileappproto1.ui.viewmodels

import android.content.Context
import android.util.Log
import com.giis.mobileappproto1.ui.activities.NotificationsActivity
import com.giis.mobileappproto1.data.models.DateStatus
import com.giis.mobileappproto1.utils.MyDate
import com.giis.mobileappproto1.data.models.NDateCategory
import com.giis.mobileappproto1.data.models.NType
import com.giis.mobileappproto1.data.models.NotificationDTO
import com.giis.mobileappproto1.data.repositories.AppRepository
import com.giis.mobileappproto1.utils.globalTag
import java.util.PriorityQueue
import javax.inject.Inject

open class SharedNotificationsViewModel @Inject constructor(private val repository: AppRepository) :
    BaseViewModel() {

    private fun fetchAllProcessedNotifications(context: Context): List<NotificationDTO> {
        val nList = fetchAllRawNotifications(context)
        nList.forEachIndexed { _, notificationDTO ->
            val now = java.util.Date() //System.currentTimeMillis()
            val result: DateStatus =
                MyDate(now.time) - MyDate(notificationDTO.notificationPostedTime)
            notificationDTO.notificationPostedTimeStatus = result
        }
        // NotificationsActivity.allTabTextReference!!.text = "All(${nList.size})"
        val unReadCountOnly = nList.filter { !it.isRead }.size
        NotificationsActivity.allCount = unReadCountOnly// nList.size
        return nList
    }

    private fun fetchAllRawNotifications(context: Context): List<NotificationDTO> =
        repository.fetchAllNotificationsDTOList(context)

    private fun fetchTaggedNotificationsOnly(context: Context): List<NotificationDTO> {
        val list = fetchAllProcessedNotifications(context)
        val filtered = list.filter { it.notifType == NType.TAGGED }
        Log.e(globalTag, "Tagged Notifications: ${filtered.size}")
        // NotificationsActivity.taggedTabTextReference!!.text = "Tagged(${filtered.size})"
        val unReadCountOnly = filtered.filter { !it.isRead }.size
        NotificationsActivity.taggedCount = unReadCountOnly // filtered.size
        return filtered
    }

    private fun fetchRequestNotificationsOnly(context: Context): List<NotificationDTO> {
        val list = fetchAllProcessedNotifications(context)
        val filtered = list.filter { it.notifType == NType.REQUEST }
        Log.e(globalTag, "Request Notifications: ${filtered.size}")
        // NotificationsActivity.reqTabTextReference!!.text = "Requests(${filtered.size})"
        val unReadCountOnly = filtered.filter { !it.isRead }.size
        NotificationsActivity.requestsCount = unReadCountOnly// filtered.size
        return filtered
    }

    //        val allNotifications = sharedViewModel.fetchAllProcessedNotifications(requireActivity())
    //        val notificationGroups: HashMap<NDateCategory, MutableList<NotificationDTO>?> = HashMap(20)
    private fun retrieveRequestedData(
        context: Context,
        listFunction: (Context) -> List<NotificationDTO>
    ): PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>> {
        val allNotifications = listFunction(context)

        val notificationGroups: HashMap<NDateCategory, MutableList<NotificationDTO>?> = HashMap(20)
        allNotifications.forEachIndexed { index, notificationDTO ->
            val title = notificationDTO.notificationPostedTimeStatus.category
            if (notificationGroups.containsKey(title)) {
                notificationGroups[title]!!.add(notificationDTO)
            } else {
                notificationGroups[title] = mutableListOf(notificationDTO)
            }
        }
        val groupsInOrder =
            PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>> { pair1, pair2 ->
                pair1.first.priority.compareTo(pair2.first.priority)
            }
        for (entry in notificationGroups.entries) {
            entry.value!!.sortWith { dto1, dto2 ->
                dto1.notificationPostedTimeStatus.difference.compareTo(dto2.notificationPostedTimeStatus.difference)
            }
            groupsInOrder.add(entry.toPair())
        }
        return groupsInOrder
    }

    fun requestNotificationData(
        context: Context,
        requestKeyTag: String
    ): PriorityQueue<Pair<NDateCategory, MutableList<NotificationDTO>?>>? {
        return when (requestKeyTag) {
            "ALL" -> this.retrieveRequestedData(
                context = context,
                listFunction = this::fetchAllProcessedNotifications
            )

            "TAG" -> this.retrieveRequestedData(
                context = context,
                listFunction = this::fetchTaggedNotificationsOnly
            )

            "REQ" -> this.retrieveRequestedData(
                context = context,
                listFunction = this::fetchRequestNotificationsOnly
            )

            else -> null.also {
                Log.e(
                    "Invalid Request",
                    "Notification Data requested from an invalid source"
                )
            }
        }
    }

}