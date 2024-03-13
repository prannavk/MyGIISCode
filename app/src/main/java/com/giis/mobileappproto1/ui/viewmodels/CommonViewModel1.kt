package com.giis.mobileappproto1.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import com.giis.mobileappproto1.data.models.ClassDetails1DTO
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.data.models.LabelType
import com.giis.mobileappproto1.data.models.NoticeDTO
import com.giis.mobileappproto1.data.repositories.AppRepository
import java.util.Date
import javax.inject.Inject

open class CommonViewModel1 @Inject constructor(private val repository: AppRepository) :
    BaseViewModel() {

    val savedPostsData: LiveData<List<FeedPostDTO>>
        get() = fetchSavedPostsData()

    private fun fetchSavedPostsData(): LiveData<List<FeedPostDTO>> =
        repository.getAllPersistedPosts()

    fun obtainNoticeData(context: Context): List<NoticeDTO> = repository.fetchNoticeDTOList(context)

    val myFeedPostsList: MutableList<FeedPostDTO> = getMutableFeedPostHardCodedData()

    private fun getMutableFeedPostHardCodedData(): MutableList<FeedPostDTO> {
        val list = mutableListOf<FeedPostDTO>()
        list.addAll(this.provideFeedPostHardcodedData())
        return list
    }

    fun provideUpcomingClassesHardCodedData(): List<ClassDetails1DTO> {
        return listOf(
            ClassDetails1DTO("9A Maths", "Trigonometry", "08:00AM - 08:50AM", "Starting Soon"),
            ClassDetails1DTO("9A Maths", "Trigonometry", "08:50AM - 09:40AM", "Delayed"),
            ClassDetails1DTO("9A English", "Prepositions", "10:00AM - 10:50AM", "Upcoming"),
            ClassDetails1DTO("9B Chemistry", "Carbon", "10:50AM - 11:40 AM", "Upcoming")
        )
    }

    private fun provideFeedPostHardcodedData(): MutableList<FeedPostDTO> {
        return mutableListOf(
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Zaire Botosh",
                // postCreationDetails = "6B Grade, Noida, 9:00AM",
                postCreatorDetails = "6B Grade, Noida",
                postCreationDateTime = Date(),
                postLabel = LabelType.ONLY_TEXT,
                postImageStatus = false,
                postText = "We will be having Pop Quiz tomorrow in class. Please come prepared. Do not forget to bring the test copies. The mark will be updated to the internal assessments.",
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
            ),
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Rika Mendez",
                // postCreationDetails = "1B Grade, Noida, 10:00AM",
                postCreatorDetails = "1B Grade, Noida",
                postCreationDateTime = Date(),
                postImageStatus = true,
                postText = "We are going to have an orientation session for the first graders tomorrow before we begin with the day",
                postImageList = listOf(
                    "https://images.pexels.com/photos/8613089/pexels-photo-8613089.jpeg"
                ),
                postAcknowledgeCount = 233,
                postCommentsCount = 411,
                postShareCount = 81,
                postTagsBoolean = false,
                postTagsList = null,
                postPollData = null,
                postPollStatus = false,
                postScheduledDateTime = null,
                postScheduledFlag = false,
                postVideoStatus = false,
                postVideoUrlString = null,
                postCreationErrorFlag = false,
                postLabel = LabelType.IMAGES,
                postCommentsOn = true
            ),
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Zen Che",
                // postCreationDetails = "7C Grade, Bengaluru, 10:00AM",
                postCreatorDetails = "7C Grade, Bengaluru",
                postCreationDateTime = Date(123, 3, 20, 10, 20),
                postImageStatus = true,
                postText = "We are going to have a mock debate session tomorrow in class before we face the actual inter-school debate competition which is to be held soon.",
                postImageList = listOf(
                    "https://images.pexels.com/photos/3184317/pexels-photo-3184317.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/1708988/pexels-photo-1708988.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
                ),
                postAcknowledgeCount = 233,
                postCommentsCount = 411,
                postShareCount = 81,
                postTagsBoolean = true,
                postTagsList = listOf("Mithun", "Vamsi", "Adarsh", "Akshay", "Alok"),
                postPollData = null,
                postPollStatus = false,
                postScheduledDateTime = null,
                postScheduledFlag = false,
                postVideoStatus = false,
                postVideoUrlString = null,
                postCreationErrorFlag = false,
                postLabel = LabelType.IMAGES,
                postCommentsOn = true
            ),
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Alex Mathew",
                // postCreationDetails = "7C Grade, Bengaluru, 10:00AM",
                postCreatorDetails = "7C Grade, Bengaluru",
                postCreationDateTime = Date(123, 4, 8, 9, 10),
                postImageStatus = true,
                postText = "We are going to have a mock debate session tomorrow in class before we face the actual inter-school debate competition which is to be held soon.",
                postImageList = listOf(
                    "https://images.pexels.com/photos/2061901/pexels-photo-2061901.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/2826131/pexels-photo-2826131.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/7973212/pexels-photo-7973212.jpeg?auto=compress&cs=tinysrgb&w=600"
                ),
                postAcknowledgeCount = 233,
                postCommentsCount = 411,
                postShareCount = 81,
                postTagsBoolean = true,
                postTagsList = listOf("Miss Sheena", "Elliot Smith", "Ken Carson"),
                postPollData = null,
                postPollStatus = false,
                postScheduledDateTime = null,
                postScheduledFlag = false,
                postVideoStatus = false,
                postVideoUrlString = null,
                postCreationErrorFlag = false,
                postLabel = LabelType.IMAGES,
                postCommentsOn = true
            ),
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Kelly Madison",
                // postCreationDetails = "7C Grade, Bengaluru, 10:00AM",
                postCreatorDetails = "7C Grade, Bengaluru",
                postCreationDateTime = Date(123, 2, 3, 8, 10),
                postImageStatus = true,
                postText = "We are going to have a mock debate session tomorrow in class before we face the actual inter-school debate competition which is to be held soon.",
                postImageList = listOf(
                    "https://images.pexels.com/photos/8344905/pexels-photo-8344905.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/8349431/pexels-photo-8349431.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/8349233/pexels-photo-8349233.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/1709003/pexels-photo-1709003.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
                ),
                postAcknowledgeCount = 233,
                postCommentsCount = 411,
                postShareCount = 81,
                postTagsBoolean = false,
                postTagsList = null,
                postPollData = null,
                postPollStatus = false,
                postScheduledDateTime = null,
                postScheduledFlag = false,
                postVideoStatus = false,
                postVideoUrlString = null,
                postCreationErrorFlag = false,
                postLabel = LabelType.IMAGES,
                postCommentsOn = true
            ),
            FeedPostDTO(
                postId = 0L,
                postCreatorName = "Jim Walter",
                // postCreationDetails = "7C Grade, Bengaluru, 10:00AM",
                postCreatorDetails = "7C Grade, Bengaluru",
                postCreationDateTime = Date(122, 12, 3, 7, 40),
                postImageStatus = true,
                postText = "We are going to have a mock debate session tomorrow in class before we face the actual inter-school debate competition which is to be held soon.",
                postImageList = listOf(
                    "https://images.pexels.com/photos/7092613/pexels-photo-7092613.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/289737/pexels-photo-289737.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/5905445/pexels-photo-5905445.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/8199165/pexels-photo-8199165.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    "https://images.pexels.com/photos/955389/pexels-photo-955389.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
                ),
                postAcknowledgeCount = 233,
                postCommentsCount = 411,
                postShareCount = 81,
                postTagsBoolean = true,
                postTagsList = listOf(
                    "John Odell",
                    "Smith Jones",
                    "Abhishek Singh",
                    "Vishnu Parmal"
                ),
                postPollData = null,
                postPollStatus = false,
                postScheduledDateTime = null,
                postScheduledFlag = false,
                postVideoStatus = false,
                postVideoUrlString = null,
                postCreationErrorFlag = false,
                postLabel = LabelType.IMAGES,
                postCommentsOn = true
            )
        )
    }

}