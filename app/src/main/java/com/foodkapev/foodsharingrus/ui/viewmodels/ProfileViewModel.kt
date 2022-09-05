package com.foodkapev.foodsharingrus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodkapev.foodsharingrus.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel() {

    var firebaseUser: FirebaseUser? = null
    private var profileId: String? = null

    private var _userData: MutableLiveData<User> = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData


    var followStatus: Boolean = false

    fun getUser() {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(profileId.toString())
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        _userData.value = user
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getPosts() {

    }

    fun followStatus() {
        val followingRef = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileId.toString()).exists())
                    followStatus = true
                else if (profileId != firebaseUser?.uid) {
                    followStatus = false
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}