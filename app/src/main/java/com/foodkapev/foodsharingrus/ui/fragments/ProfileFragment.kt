package com.foodkapev.foodsharingrus.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.AccountSettingsActivity
import com.foodkapev.foodsharingrus.ui.adapters.ImagesAdapter
import com.foodkapev.foodsharingrus.domain.Post
import com.foodkapev.foodsharingrus.domain.User
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postsListUploadedImages: List<Post>? = null
    var imagesAdapterUploadedImages: ImagesAdapter? = null

    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
            this.profileId = pref.getString("profileId", "none")!!

        profileId != firebaseUser.uid
        checkFollowAndFollowingBtnStatus()

        //Uploaded images
        val recyclerViewUploadedImages: RecyclerView =
            view.findViewById(R.id.recycler_view_uploaded_pic)
        recyclerViewUploadedImages.setHasFixedSize(true)

        val linearLayoutManagerUploadedImages = LinearLayoutManager(context)
        recyclerViewUploadedImages.layoutManager = linearLayoutManagerUploadedImages

        postsListUploadedImages = ArrayList()
        imagesAdapterUploadedImages =
            context?.let { ImagesAdapter(it, postsListUploadedImages as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = imagesAdapterUploadedImages

        binding.optionsView.setOnClickListener {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        binding.helpView.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy")
            )
            startActivity(browserIntent)
        }
        userInfo()
        currentUserPosts()

    }

    private fun checkFollowAndFollowingBtnStatus() {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    with(binding) {
                        if (dataSnapshot.child(profileId).exists())
                            editAccountSettingsBtn.text = "Following"
                        else if (profileId != firebaseUser.uid) {
                            editAccountSettingsBtn.visibility = View.VISIBLE
                            editAccountSettingsBtn.text = "Follow"
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }

    private fun currentUserPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postsListUploadedImages as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post?.publisher == profileId)
                            (postsListUploadedImages as ArrayList<Post>).add(post)
                        Collections.reverse(postsListUploadedImages)

                        imagesAdapterUploadedImages!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    with(binding) {
                        Picasso.get().load(user?.image).placeholder(R.drawable.profile)
                            .into(proImageProfileFragment)
                        profileFragmentUsername.text = user?.username
                        fullNameProfileFragment.text = user?.fullname
                        bioProfileFragmentText.text = user?.bio
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}