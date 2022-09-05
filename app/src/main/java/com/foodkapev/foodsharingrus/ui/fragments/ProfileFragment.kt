package com.foodkapev.foodsharingrus.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.FragmentProfileBinding
import com.foodkapev.foodsharingrus.domain.models.Post
import com.foodkapev.foodsharingrus.domain.models.User
import com.foodkapev.foodsharingrus.ui.adapters.ProfilePostsAdapter
import com.foodkapev.foodsharingrus.ui.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var profileId: String
    private var firebaseUser: FirebaseUser? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding

    var postsListUploadedImages: List<Post>? = null
    var profileAdapter: ProfilePostsAdapter? = null

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUser()
        profileViewModel.followStatus()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
            this.profileId = pref.getString("profileId", "none")!!

        firebaseUser = profileViewModel.firebaseUser

        if (profileViewModel.followStatus)
            binding?.editAccountSettingsBtn?.text = getString(R.string.profile_fragment_following)
        else {
            binding?.editAccountSettingsBtn?.visibility = View.VISIBLE
            binding?.editAccountSettingsBtn?.text = getString(R.string.profile_fragment_follow)
        }

        //Uploaded images
        val recyclerViewUploadedImages: RecyclerView =
            view.findViewById(R.id.recycler_view_uploaded_pic)
        recyclerViewUploadedImages.setHasFixedSize(true)

        val linearLayoutManagerUploadedImages = LinearLayoutManager(context)
        recyclerViewUploadedImages.layoutManager = linearLayoutManagerUploadedImages

        postsListUploadedImages = ArrayList()
        profileAdapter =
            context?.let { ProfilePostsAdapter(it, postsListUploadedImages as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = profileAdapter
        binding?.optionsView?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }

        binding?.helpView?.setOnClickListener {
            openPrivacyPolicy()
        }
        userInfo()
        currentUserPosts()
    }

    private fun openPrivacyPolicy() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://raw.githubusercontent.com/5cr1p7/FoodsharingRus/master/PrivacyPolicy")
        )
        startActivity(browserIntent)
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

                        profileAdapter!!.notifyDataSetChanged()
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
                        this?.proImageProfileFragment?.let {
                            Glide.with(requireContext()).load(user?.image)
                                .placeholder(R.drawable.profile).into(it)
                        }

                        this?.profileFragmentUsername?.text = user?.username ?: "Empty"
                        this?.fullNameProfileFragment?.text = user?.fullname ?: "Empty"
                        this?.bioProfileFragmentText?.text = user?.bio ?: "Empty"
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
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser?.uid)
        pref?.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}