package com.foodkapev.foodsharingrus.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.FragmentPostDetailsBinding
import com.foodkapev.foodsharingrus.domain.models.Post
import com.foodkapev.foodsharingrus.presentation.adapters.PostDetailsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {
    private var postDetailsAdapter: PostDetailsAdapter? = null
    private var postList: MutableList<Post>? = null
    private var postId: String = ""
    private val args: PostDetailsFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentPostDetailsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = args.postId

        binding.recyclerViewPostDetails.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewPostDetails.layoutManager = linearLayoutManager

        postList = ArrayList()
        postDetailsAdapter = context?.let { PostDetailsAdapter(it, postList as ArrayList<Post>) }
        binding.recyclerViewPostDetails.adapter = postDetailsAdapter

        retrievePosts()
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId)

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(Post::class.java)

                postList?.clear()
                postList!!.add(post!!)
                postDetailsAdapter!!.notifyDataSetChanged()

                binding.postTitleBar.text = post.title
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}