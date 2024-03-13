package com.giis.mobileappproto1.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import javax.annotation.Nullable

@Parcelize
@Entity(tableName = "PersistedPosts")
data class FeedPostDTO(

    @PrimaryKey(autoGenerate = true)
    val postId: Long,
    val postCreatorName: String,
    val postCreatorDetails: String,
    val postCreationDateTime: Date,

    var postText: String,
    var postLabel: LabelType,
    var postImageStatus: Boolean,
    var postTagsBoolean: Boolean,
    var postCommentsCount: Int,
    var postCommentsOn: Boolean,
    var postAcknowledgeCount: Int,
    var postShareCount: Int,
    var postPollStatus: Boolean,
    var postVideoStatus: Boolean,
    var postScheduledFlag: Boolean,
    var postCreationErrorFlag: Boolean,

    @Nullable
    var postVideoUrlString: String?,

    @Nullable
    val postScheduledDateTime: Date?,

    @Nullable
    val postImageList: List<String>?, // (If postImageStatus is true -> contains list of glide urls)

    @Nullable
    val postTagsList: List<String>?, // If postTagsBoolean is true -> contains list of tags

    @Nullable
    var postPollData: HashMap<String, Int>? // If postPollStatus is true -> contains  HashMap Poll Data
) : Parcelable {
    init {
        enforceLabelConstraint()
        validatePostingDate()
        // val dateEx = Date(122, 6, 20, 10, 30)
    }

    private fun validatePostingDate() {
        if (this.postScheduledFlag) {
            if (this.postScheduledDateTime == null) {
                this.postScheduledFlag = true
            }
        }
    }

    private fun enforceLabelConstraint() {
        when (this.postLabel) {
            LabelType.ONLY_TEXT -> {
                this.postImageStatus = false
                this.postTagsBoolean = false
                this.postPollStatus = false
                this.postVideoStatus = false
                this.postCreationErrorFlag = false
            }

            LabelType.IMAGES -> {
                if (this.postImageStatus) {
                    if (this.postImageList == null) {
                        this.postImageStatus = false
                        this.postCreationErrorFlag = true
                    }
                } else {
                    this.postImageStatus = true
                }
            }

            LabelType.VIDEO -> {
                if (this.postVideoStatus) {
                    if (this.postVideoUrlString == null) {
                        this.postVideoStatus = false
                        this.postCreationErrorFlag = true
                    }
                } else {
                    this.postVideoStatus = true
                }
            }

            LabelType.POLL -> {
                if (this.postPollStatus) {
                    if (this.postPollData == null) {
                        this.postPollStatus = false
                        this.postCreationErrorFlag = true
                    }
                } else {
                    this.postPollStatus = true
                }
            }
        }
    }
}
