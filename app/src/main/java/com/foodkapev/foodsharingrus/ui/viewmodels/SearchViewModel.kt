package com.foodkapev.foodsharingrus.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foodkapev.foodsharingrus.domain.models.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(application: Application): AndroidViewModel(application) {

    private val searchTextMutableState = MutableLiveData<Post?>()

    val searchText: LiveData<Post?>
        get() = searchTextMutableState

//    private var mPost: MutableList<Post>? = null

    fun searchPost(input: String) {
        viewModelScope.launch {
            val query = FirebaseDatabase.getInstance().reference
                .child("Posts")
                .orderByChild("title")
                .startAt(input)
                .endAt(input + "\uf8ff")

            query.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnaphot: DataSnapshot) {
//                    mPost?.clear()
                    for (snapshot in dataSnaphot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post != null) {
//                            mPost?.add(post)
                            searchTextMutableState.postValue(post)
                            Timber.d(searchTextMutableState.toString())
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }
}