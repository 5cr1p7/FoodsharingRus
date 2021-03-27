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
        recyclerViewUploadedImages.layoutManager = linearLayoutManagerUploadedImages

        postsListUploadedImages = ArrayList()
        imagesAdapterUploadedImages = context?.let { ImagesAdapter(it, postsListUploadedImages as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = imagesAdapterUploadedImages

        view.options_view.setOnClickListener {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        view.help_view.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy"))
            startActivity(browserIntent)
        }
        userInfo()
        currentUserPosts()

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

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_fragment)
                    view?.profile_fragment_username?.text = user?.getUsername()
                    view?.full_name_profile_fragment?.text = user?.getFullname()
                    view?.bio_profile_fragment_text?.text = user?.getBio()
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