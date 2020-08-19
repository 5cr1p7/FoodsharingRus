package com.foodkapev.foodsharingrus.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.AccountSettingsActivity
import com.foodkapev.foodsharingrus.Adapter.ImagesAdapter
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.Model.User
import com.foodkapev.foodsharingrus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postsListUploadedImages: List<Post>? = null
    var imagesAdapterUploadedImages: ImagesAdapter? = null

//    var postsListSavedImages: List<Post>? = null
//    var imagesAdapterSavedImages: ImagesAdapter? = null
//    var currentUserSavedImages: List<String>? = null

//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
            this.profileId = pref.getString("profileId", "none")!!

        profileId != firebaseUser.uid
            checkFollowAndFollowingBtnStatus()

        //Uploaded images
        val recyclerViewUploadedImages: RecyclerView
        recyclerViewUploadedImages = view.findViewById(R.id.recycler_view_uploaded_pic)
        recyclerViewUploadedImages.setHasFixedSize(true)

        val linearLayoutManagerUploadedImages: LinearLayoutManager = LinearLayoutManager(context)
//        linearLayoutManagerUploadedImages.reverseLayout = true
//        linearLayoutManagerUploadedImages.stackFromEnd = true
        recyclerViewUploadedImages.layoutManager = linearLayoutManagerUploadedImages

        postsListUploadedImages = ArrayList()
        imagesAdapterUploadedImages = context?.let { ImagesAdapter(it, postsListUploadedImages as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = imagesAdapterUploadedImages

//        postsListSavedImages = ArrayList()
//        imagesAdapterSavedImages = context?.let { ImagesAdapter(it, postsListSavedImages as ArrayList<Post>) }
//        recyclerViewSavedImages.adapter = imagesAdapterSavedImages

//        recyclerViewSavedImages.visibility = View.GONE
//        recyclerViewUploadedImages.visibility = View.VISIBLE
//
//        val uploadedImagesBtn: ImageView
//        uploadedImagesBtn = view.findViewById(R.id.images_grid_view_btn)
//        uploadedImagesBtn.setOnClickListener {
//            recyclerViewSavedImages.visibility = View.GONE
//            recyclerViewUploadedImages.visibility = View.VISIBLE
//        }
//
//        val savedImagesBtn: ImageView
//        savedImagesBtn = view.findViewById(R.id.images_save_btn)
//        savedImagesBtn.setOnClickListener {
//            recyclerViewSavedImages.visibility = View.VISIBLE
//            recyclerViewUploadedImages.visibility = View.GONE
//        }

//        view.total_followers.setOnClickListener {
//            val intent = Intent(context, ShowUsersActivity::class.java)
//            intent.putExtra("id", profileId)
//            intent.putExtra("title", "Followers")
//            startActivity(intent)
//        }

//        view.total_following.setOnClickListener {
//            val intent = Intent(context, ShowUsersActivity::class.java)
//            intent.putExtra("id", profileId)
//            intent.putExtra("title", "Following")
//            startActivity(intent)
//        }


        view.options_view.setOnClickListener {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        view.help_view.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy"))
            startActivity(browserIntent)
        }

//        getFollowers()
//        getFollowing()
        userInfo()
        currentUserPosts()
//        getTotalNumberOfPosts()
//        currentUserSaves()

        return view
    }

    private fun checkFollowAndFollowingBtnStatus() {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(profileId).exists())
                        view?.edit_account_settings_btn?.text = "Following"
                    else if (profileId != firebaseUser.uid) {
                        view?.edit_account_settings_btn?.visibility = View.VISIBLE
                        view?.edit_account_settings_btn?.text = "Follow"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }

//    private fun getFollowers() {
//        val followersRef = FirebaseDatabase.getInstance().reference
//            .child("Follow").child(profileId)
//            .child("Followers")
//
//        followersRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    view?.total_followers?.text = dataSnapshot.childrenCount.toString()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

//    private fun getFollowing() {
//        val followingRef = FirebaseDatabase.getInstance().reference
//            .child("Follow").child(profileId)
//            .child("Following")
//
//        followingRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    view?.total_following?.text = dataSnapshot.childrenCount.toString()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

    private fun currentUserPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postsListUploadedImages as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.getPublisher() == profileId)
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
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (context != null)
//                    return

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_fragment)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_profile_fragment?.text = user.getFullname()
                    view?.bio_profile_fragment_text?.text = user.getBio()
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

//    private fun getTotalNumberOfPosts() {
//        var postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
//
//        postsRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    var postCounter = 0
//
//                    for (snapshot in dataSnapshot.children) {
//                        val post = snapshot.getValue(Post::class.java)
//                        if (post!!.getPublisher() == profileId) {
//                            postCounter++
//
//                        }
//                    }
//                    total_posts.text = postCounter.toString()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

//    private fun currentUserSaves() {
//        currentUserSavedImages = ArrayList()
//
//        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves")
//            .child(firebaseUser.uid)
//
//        savesRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (snapshot in dataSnapshot.children) {
//                        (currentUserSavedImages as ArrayList<String>).add(snapshot.key!!)
//                    }
//
//                    readSavedImagesData()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

//    private fun readSavedImagesData() {
//        var postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
//
//        postsRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    (postsListSavedImages as ArrayList<Post>).clear()
//                    for (snapshot in dataSnapshot.children) {
//                        val post = snapshot.getValue(Post::class.java)
//
//                        for (key in currentUserSavedImages!!) {
//                            if (post!!.getPostId() == key) {
//                                (postsListSavedImages as ArrayList<Post>).add(post)
//                            }
//                        }
//                    }
//
////                    imagesAdapterSavedImages!!.notifyDataSetChanged()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

//    private fun addNotification() {
//        val notificationsRef = FirebaseDatabase.getInstance().reference
//            .child("Notifications")
//            .child(profileId)
//
//        val notificationsMap = HashMap<String, Any>()
//        notificationsMap["userId"] = firebaseUser!!.uid
//        notificationsMap["text"] = "started following you"
//        notificationsMap["postId"] = ""
//        notificationsMap["isPost"] = false
//
//        notificationsRef.push().setValue(notificationsMap)
//
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}