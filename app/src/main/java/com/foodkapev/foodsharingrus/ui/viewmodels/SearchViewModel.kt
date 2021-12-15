package com.foodkapev.foodsharingrus.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel(application: Application): AndroidViewModel(application) {
    private val _searchText = MutableLiveData<String>()

    val searchText: LiveData<String> = _searchText
}