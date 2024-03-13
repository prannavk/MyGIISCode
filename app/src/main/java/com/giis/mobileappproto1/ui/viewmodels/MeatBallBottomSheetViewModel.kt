package com.giis.mobileappproto1.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.data.repositories.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MeatBallBottomSheetViewModel @Inject constructor(private val repository: AppRepository) :
    BaseViewModel() {

    fun saveThisPostInDb(postDTO: FeedPostDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.persistThisPost(postDTO)
        }
    }

    fun removeThisPostFromDb(feedPostDTO: FeedPostDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeThisPost(feedPostDTO)
        }
    }


}