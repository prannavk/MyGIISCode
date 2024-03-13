package com.giis.mobileappproto1.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.giis.mobileappproto1.GIISSessionTokenDataManager
import com.giis.mobileappproto1.ui.adapters.LabelsAdapter
import com.giis.mobileappproto1.ui.bottomsheets.PeopleTagsBottomSheet
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.data.models.LabelType
import com.giis.mobileappproto1.data.models.PersonSelectTagDTO
import com.giis.mobileappproto1.data.models.UploadFileApiResponse
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.databinding.ActivityCreatePostBinding
import com.giis.mobileappproto1.ui.adapters.ImageUploadAdapter
import com.giis.mobileappproto1.ui.dialogs.StdLoadingDialog
import com.giis.mobileappproto1.ui.fragments.LoginCredentialsFragment
import com.giis.mobileappproto1.ui.viewmodels.CreatePostViewModel
import com.giis.mobileappproto1.utils.Commons
import com.giis.mobileappproto1.utils.createFailResultCode
import com.giis.mobileappproto1.utils.createPassResultCode
import com.giis.mobileappproto1.utils.globalTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity() {

    companion object {
        const val dTag = "Multi_Part_Issue"
    }

    private lateinit var binding: ActivityCreatePostBinding

    private var selectedTagsData: MutableList<PersonSelectTagDTO> = mutableListOf()
    private var unSelectedTagsData: MutableList<PersonSelectTagDTO> = mutableListOf()
    private var tagOverflowCounter = 1
    private var tagOverflowFlag = false

    private var keyTokenString: String = ""
    private var keySessionData: VerifiedLoginDataDTO? = null
    private var validTokenReceivedFlag = false
    private var successfulMediaUploadFlag = false
    private var successfulMediaUploadCounter = 0
    private var imageUploadUriList: MutableList<Uri?> = mutableListOf()
    private val imagesAdapter =
        ImageUploadAdapter(this, imageUploadUriList, this::deQueueMediaAndCheck)
    private var postUploadLoadingDialog: StdLoadingDialog? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                actionOnMediaEnqueue(uri)
                if (binding.uploadedImagesRv.visibility == View.GONE) {
                    binding.uploadedImagesRv.visibility = View.VISIBLE
                }
                // On Media Addition
                imagesAdapter.notifyDataSetChanged()
            } else {
                Log.e("PhotoPicker", "No media selected")
            }
        }

    private fun actionOnMediaEnqueue(uri: Uri) {
        Log.e("PhotoPicker", "Selected URI: $uri")
        this.imageUploadUriList.add(uri)
        successfulMediaUploadFlag = true
        successfulMediaUploadCounter += 1
    }

    private fun deQueueMediaAndCheck(position: Int) {
        successfulMediaUploadCounter -= 1
        imageUploadUriList.removeAt(position)
        if (successfulMediaUploadCounter == 0) {
            successfulMediaUploadFlag = false
        }
    }

    @Inject
    lateinit var viewModel: CreatePostViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_create_post)
        postUploadLoadingDialog = StdLoadingDialog(this)
        binding =
            DataBindingUtil.setContentView(this@CreatePostActivity, R.layout.activity_create_post)

        necessaryViewBindingsAndListenerConfigurations()
        actionToolBarConfiguration()
        getPostCreatorSessionValidityToken()
        configureUnSelectTagsData()
        configureTagCrossSelectTextViewActions()
        setMediaLabelNotActivatedListener()

        // OnClick of this will validate user Interaction and Create the Post For User
        binding.createThisPostButton.setOnClickListener {
            performValidationChainAndBehave(it)
        }

    }

    private fun performValidationChainAndBehave(clickView: View) {
        if (validTokenReceivedFlag) {
            if (binding.createPostTextEt.text.toString() == "") {
                Commons.simpleSnackAlert(this, clickView, "Please enter your thoughts first")
            } else {
                spinnerValidationChain1(clickView)
            }
        } else {
            Commons.simpleSnackAlert(this, clickView, "Cannot Post Right Now, please Try again")
        }
    }

    private fun spinnerValidationChain1(clickView: View) {
        val postToSpinnerPos = binding.postToSpinner.selectedItemPosition
        if (postToSpinnerPos != AdapterView.INVALID_POSITION) {
            val groupSelect =
                binding.postToSpinner.getItemAtPosition(postToSpinnerPos) as String
            if (groupSelect == "Please Select") {
                Commons.toastIt(this, "Please select an appropriate group to post to")
            } else {
                val spinnerPosition = binding.labelSpinner.selectedItemPosition
                if (spinnerPosition != AdapterView.INVALID_POSITION) {
                    spinnerValidationChain2(spinnerPosition, clickView)
                } else {
                    Commons.simpleSnackAlert(this, clickView, "Picked Label Not Registered")
                }
            }
        } else {
            Commons.simpleSnackAlert(this, clickView, "Picked Group Not Registered")
        }
    }

    private fun spinnerValidationChain2(spinnerPosition: Int, clickView: View) {
        val selectItem =
            binding.labelSpinner.getItemAtPosition(spinnerPosition) as String

        val labelUnSelectBoolean = selectItem == "Please Select"
        val mediaLabelBoolean =
            (selectItem == "Video" || selectItem == "Image") && (binding.uploadedImagesRv.visibility == View.GONE && !successfulMediaUploadFlag && successfulMediaUploadCounter == 0)
        val otherLabelBoolean = selectItem == "Podcast" || selectItem == "Audio"
        val mediaTextBoolean =
            selectItem == "Text" && successfulMediaUploadFlag && successfulMediaUploadCounter > 0
        val tagsProblem =
            (selectItem == "Text" && selectedTagsData.size > 0) || (selectItem == "Text" && mediaTextBoolean)

        if (labelUnSelectBoolean) {
            Commons.toastIt(this, "Please Pick The Post Label")
        } else if (mediaLabelBoolean) {
            Log.e(globalTag, "correct place")
            Commons.toastIt(this, "Make sure You have selected Media To Upload")
        } else if (otherLabelBoolean) {
            Commons.toastIt(this, "Uploading this content is not possible right now")
        } else if (mediaTextBoolean) {
            Commons.toastIt(this, "Cannot Upload Images with Text Label")
        } else if (tagsProblem) {
            Commons.toastIt(this, "Cannot Have Tags without Media")
        } else {
            // As of Now label should either be text or Image with imageUploadSuccessful
            // All Validations are positive for Post Creation, if execution reached here
            // Create FeedPostDTO object (tags and all booleans set to false by default)
            val enteredPostText = binding.createPostTextEt.text.toString()
            val commentsChecked = binding.commentsSwitch.isChecked

            when (selectItem) {
                "Text" -> {
                    val postDTO = provideFeedPostDTO(enteredPostText, commentsChecked)
                    finishWithSuccessResult(postDTO)
                }

                "Image" -> {
                    if (successfulMediaUploadCounter == 0) {
                        Commons.toastIt(this, "Make sure You have selected Media To Upload")
                    } else {
                        var tagsBool = false
                        var allTagsList: List<String>? = null

                        if ((selectedTagsData.size) > 0) {
                            tagsBool = true
                            allTagsList =
                                selectedTagsData.map { dto -> dto.pfpUrl }
                                    .toList()
                        }

                        // MULTIPART REQUEST API CALL

                        try {
                            // Anim start
                            LoginCredentialsFragment.showProgressAnim(
                                postUploadLoadingDialog!!,
                                10000L
                            )
                            // Api Call
                            viewModel.uploadMedia(
                                this.imageUploadUriList,
                                this@CreatePostActivity
                            ).observe(this@CreatePostActivity) {
                                // Stop Anim
                                if (postUploadLoadingDialog!!.isShowing) {
                                    postUploadLoadingDialog!!.cancel()
                                }
                                // Observer Logic
                                handleTheResult(
                                    it,
                                    tagsBool,
                                    allTagsList,
                                    enteredPostText,
                                    commentsChecked
                                )
                            }
                        } catch (ex: Exception) {
                            Log.e(
                                dTag,
                                "EXCEPTION WHILE UPLOADING IMAGES \nIssue: ${ex.message}"
                            )
                            if (postUploadLoadingDialog!!.isShowing) {
                                postUploadLoadingDialog!!.cancel()
                            }
                        }
                    }
                }

                else -> {
                    Commons.simpleSnackAlert(this, clickView, "Cannot post such content yet")
                }

            }

        }
    }

    private fun handleTheResult(
        result: Result<UploadFileApiResponse>?,
        tagsBool: Boolean,
        allTagsList: List<String>?,
        enteredPostText: String,
        commentsChecked: Boolean
    ) {
        val it = result
        when (it) {
            is Result.Success -> {
                it.data?.let { response ->
                    val imageUrlsList = mutableListOf<String>()
                    imageUrlsList.addAll(response.filepathStringsArrayList.map { value ->
                        value.fileLocation ?: "WRONG_PATH"
                    })

                    val createdPostDTO = provideFeedPostDTO(
                        tagsBoolean = tagsBool,
                        tagsList = allTagsList,
                        imagesList = imageUrlsList.toList(),
                        postText = enteredPostText,
                        commentsBool = commentsChecked
                    )

                    finishWithSuccessResult(createdPostDTO)

                } ?: run {
                    Commons.toastIt(
                        this,
                        "Could not update post details, please try again"
                    )
                }
            }

            is Result.Failure -> {
                Commons.toastIt(this, "Failure in uploading Image Data")
            }

            is Result.NetworkError -> {
                Commons.toastIt(this, "Please Check you Internet Connection")
            }

            else -> {}
        }
    }

    private fun finishWithSuccessResult(createdFeedPostDTO: FeedPostDTO?) {
        createdFeedPostDTO?.let {
            val goBackWithResult = Intent()
            goBackWithResult.putExtra("newFeedPost", createdFeedPostDTO)
            setResult(createPassResultCode, goBackWithResult)
            Log.e(globalTag, "Created Post Checked and Sent - AR")
            finish()
        } ?: run { Log.e(globalTag, "Null obtained instead of Created PostDTO") }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureTagCrossSelectTextViewActions() {
        var drawable: Drawable?
        binding.tagSelect1.viewTreeObserver.addOnGlobalLayoutListener {
            drawable = binding.tagSelect1.compoundDrawables[2]
            drawable?.let {
                binding.tagSelect1.setOnTouchListener(
                    SelectTextViewOnTouch(
                        it,
                        targetPos = 0u
                    )
                )
                binding.tagSelect2.setOnTouchListener(
                    SelectTextViewOnTouch(
                        it,
                        targetPos = 1u
                    )
                )
            } ?: kotlin.run { Log.e("TagDrawable", "Issue with Tag Drawable") }
        }
    }

    private fun programTagPeopleSpinnerBtnListener() {
        val spinnerPosition = binding.labelSpinner.selectedItemPosition
        if (spinnerPosition != AdapterView.INVALID_POSITION) {
            val selectItem =
                binding.labelSpinner.getItemAtPosition(spinnerPosition) as String
            if (selectItem == "Image" || selectItem == "Video") {
                setMediaLabelActivatedListener()
            } else {
                setMediaLabelNotActivatedListener()
            }
        } else {
            setMediaLabelNotActivatedListener()
        }
    }

    private fun setMediaLabelActivatedListener() {
        binding.tagPeopleSpinnerBtn.setOnClickListener {
            val bottomSheetForOptions = PeopleTagsBottomSheet(unSelectedTagsData.toList()).apply {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
                this.onDismissListener = { data ->
                    data?.let {
                        Log.e(
                            "Tags Issue",
                            "CPA: received: ${data.first} - position and dto: ${data.second.pfpUrl} - OK"
                        )
                        //check if tagselect1 is gone? If yes, (it means its the first item) => (add dto to select list, remove from unselect list and add text to this tv, make it visible)
                        //If No: check if tagselect2 is gone? If yes, (it means its the second item) => (add dto to select list, remove from unselect list and add text to this tv, make it visible)
                        //If still No: Check if rest counter is visible: If not visible, (it means its the third item) => (add dto to select list, remove from unselect list and start rest counter from 1, make it visible)
                        //else => (it is the 4+ item) => (add dto to select list, remove from unselect list and increment rest counter)
                        if (binding.tagSelect1.visibility == View.GONE) {
                            makeItSelected(it.second)
                            binding.tagSelect1.text = it.second.pfpUrl
                            binding.tagSelect1.visibility = View.VISIBLE
                        } else if (binding.tagSelect2.visibility == View.GONE) {
                            makeItSelected(it.second)
                            binding.tagSelect2.text = it.second.pfpUrl
                            binding.tagSelect2.visibility = View.VISIBLE
                        } else if (binding.tagSelectRestCounter.visibility == View.GONE) {
                            makeItSelected(it.second)
                            binding.tagSelectRestCounter.text = "+$tagOverflowCounter"
                            tagOverflowFlag = true
                            binding.tagSelectRestCounter.visibility = View.VISIBLE
                        } else {
                            makeItSelected(it.second)
                            binding.tagSelectRestCounter.text = "+${++tagOverflowCounter}"
                        }
                    } ?: kotlin.run { Log.e("Tags Issue", "CPA: Null Pair received") }

                }
            }
            bottomSheetForOptions.show(supportFragmentManager, "")
        }
    }

    private fun setMediaLabelNotActivatedListener() {
        binding.tagPeopleSpinnerBtn.setOnClickListener {
            Commons.simpleSnackAlert(
                this@CreatePostActivity,
                it,
                "Please Select Image Or Video Label To Tag"
            )
        }
    }


    private fun configureUnSelectTagsData() {
        unSelectedTagsData.addAll(viewModel.fetchAllSelectPersonTagsData(this))
    }

    private fun getPostCreatorSessionValidityToken() {
        val sessionManager = GIISSessionTokenDataManager(applicationContext)
        val sessionDto = sessionManager.getVerifiedSessionDTO()
        val sessionToken = sessionDto?.let {
            keySessionData = it
            binding.creatorName.text = it.nameOfPerson ?: "This Person"
            it.token
        } ?: run {
            Log.e(
                globalTag,
                "SessionDto is Null"
            ); null
        }
        sessionToken?.let { token ->
            if (token != "failure" && token != "Failure" && token.length > 1) {
                keyTokenString = token
                binding.createThisPostButton.isEnabled = true
                validTokenReceivedFlag = true
            } else {
                Toast.makeText(
                    this@CreatePostActivity,
                    "Cannot Post Right Now, please Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: run {
            Toast.makeText(
                this@CreatePostActivity,
                "Cannot Post Right Now, please Try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun actionToolBarConfiguration() {
        setSupportActionBar(binding.createPostToolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        binding.tbIncl.tbTitleTextView.text = getString(R.string.create_post_title)
        binding.tbIncl.tbHomeasupButton.setOnClickListener {
            setResult(
                createFailResultCode,
                Intent()
            ); finish()
        }
    }

    private fun necessaryViewBindingsAndListenerConfigurations() {
        // Media Uploads Shower
        binding.uploadedImagesRv.apply {
            adapter = imagesAdapter
            layoutManager =
                LinearLayoutManager(this@CreatePostActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        // Labels Spinner
        val postLabelsData2 = viewModel.fetchLabelOptionsData(this@CreatePostActivity)
        val adapter = LabelsAdapter(
            postLabelsData2,
            this,
            affectOtherViewsOnChange = this::programTagPeopleSpinnerBtnListener
        )
        binding.labelSpinner.apply {
            this.adapter = adapter
            this.setOnTouchListener(adapter::onTouch)
        }

        // Post To Groups Spinner
        val postToData = viewModel.fetchPostToOptionsData(this@CreatePostActivity)
        val adapter2 = LabelsAdapter(postToData, this, affectOtherViewsOnChange = null)
        binding.postToSpinner.apply {
            this.adapter = adapter2
            this.setOnTouchListener(adapter2::onTouch)
        }

        // Media Upload Launcher Button
        binding.mediaPickerLaunchBtn.setOnClickListener {
            // basicImageContract.launch("image/*")
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Schedule Post Switch
        binding.schedulePostSwitch.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Please Post Now", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(500)
                    // binding.schedulePostSwitch.isChecked = false
                    btnView.isChecked = false
                }
            }
        }
    }

//    private fun uploadTheseImages() {
//        lifecycleScope.launch {
//            this@CreatePostActivity.imageUploadUriList.forEach { uri ->
//                uri?.let {
//                    val stream = contentResolver.openInputStream(it)
//                }
//            }
//        }
//    }

    private fun makeItSelected(dto: PersonSelectTagDTO) {
        selectedTagsData.add(dto)
        unSelectedTagsData.remove(dto)
    }

    // Overloaded
    private fun provideFeedPostDTO(postText: String, commentsBool: Boolean): FeedPostDTO {
        return FeedPostDTO(
            postId = 0L,
            postCreatorName = this.keySessionData?.nameOfPerson ?: "Aarthi Shridhar",
            // postCreationDetails = "6B Grade, Noida, 9:00AM",
            postCreatorDetails = "6B Grade, ${this.keySessionData?.campusName ?: "GIIS"}",
            postCreationDateTime = java.util.Date(),
            postLabel = LabelType.ONLY_TEXT,
            postImageStatus = false,
            postText = postText,
            postImageList = null,
            postAcknowledgeCount = 0,
            postCommentsCount = 0,
            postShareCount = 0,
            postTagsBoolean = false,
            postTagsList = null,
            postPollData = null,
            postPollStatus = false,
            postScheduledDateTime = null,
            postScheduledFlag = false,
            postVideoStatus = false,
            postVideoUrlString = null,
            postCreationErrorFlag = false,
            postCommentsOn = commentsBool
        )
    }

    // Overloaded
    private fun provideFeedPostDTO(
        tagsBoolean: Boolean,
        tagsList: List<String>?,
        imagesList: List<String>,
        postText: String,
        commentsBool: Boolean
    ): FeedPostDTO {
        return FeedPostDTO(
            postId = 0L,
            postCreatorName = this.keySessionData?.nameOfPerson ?: "Aarthi Shridhar",
            // postCreationDetails = "6B Grade, Noida, 9:00AM",
            postCreatorDetails = "6B Grade, ${this.keySessionData?.campusName ?: "GIIS"}",
            postCreationDateTime = java.util.Date(),
            postLabel = LabelType.IMAGES,
            postImageStatus = true,
            postText = postText,
            postImageList = imagesList,
            postAcknowledgeCount = 0,
            postCommentsCount = 0,
            postShareCount = 0,
            postTagsBoolean = tagsBoolean,
            postTagsList = tagsList,
            postPollData = null,
            postPollStatus = false,
            postScheduledDateTime = null,
            postScheduledFlag = false,
            postVideoStatus = false,
            postVideoUrlString = null,
            postCreationErrorFlag = false,
            postCommentsOn = commentsBool
        )
    }


    private inner class SelectTextViewOnTouch(
        val drawable: Drawable?,
        private val targetPos: UInt
    ) :
        OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val endCrossDrawableWidth = drawable?.bounds?.width() ?: 0
            return if (event!!.x >= (v!!.width - v.paddingEnd - endCrossDrawableWidth)) {
                if (event.action == MotionEvent.ACTION_UP) {
                    val selectedCount = this@CreatePostActivity.selectedTagsData.size
                    if (selectedCount > 3) {
                        binding.tagSelectRestCounter.text = "+${--tagOverflowCounter}"
                        val removeTag =
                            this@CreatePostActivity.selectedTagsData.removeAt(targetPos.toInt())
                        shiftSelect1(removeTag)
                    } else if (selectedCount == 3) {
                        this@CreatePostActivity.tagOverflowFlag = false
                        this@CreatePostActivity.tagOverflowCounter = 1
                        val removeTag =
                            this@CreatePostActivity.selectedTagsData.removeAt(targetPos.toInt())
                        shiftSelect1(removeTag)
                        binding.tagSelectRestCounter.visibility = View.GONE
                    } else if (selectedCount == 2) {
                        val removeTag =
                            this@CreatePostActivity.selectedTagsData.removeAt(targetPos.toInt())
                        this@CreatePostActivity.unSelectedTagsData.add(removeTag)
                        binding.tagSelect1.text = selectedTagsData[0].pfpUrl
                        binding.tagSelect2.visibility = View.GONE
                    } else { // For 1
                        binding.tagSelect1.visibility = View.GONE
                        val removeTag = this@CreatePostActivity.selectedTagsData.removeAt(0)
                        this@CreatePostActivity.unSelectedTagsData.add(removeTag)
                        selectedTagsData.removeAll { 1 == 1 }
                    }
                }
                v.performClick()
                true
            } else {
                v.performClick()
                false
            }
        }

        private fun shiftSelect1(removeTag: PersonSelectTagDTO) {
            this@CreatePostActivity.unSelectedTagsData.add(removeTag)
            binding.tagSelect1.text = selectedTagsData[0].pfpUrl
            binding.tagSelect2.text = selectedTagsData[1].pfpUrl
        }

    }


}