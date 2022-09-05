package com.foodkapev.foodsharingrus.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.FragmentCommentsBinding
import com.foodkapev.foodsharingrus.domain.models.Comment
import com.foodkapev.foodsharingrus.domain.models.User
import com.foodkapev.foodsharingrus.ui.adapters.CommentsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentsFragment: Fragment(R.layout.fragment_comments) {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null
    private val args: CommentsFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentCommentsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = args.postId
        publisherId = args.publisherId

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        binding.recyclerViewComments.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentsAdapter = CommentsAdapter(requireContext(), commentList)
        binding.recyclerViewComments.adapter = commentsAdapter

        commentsUserInfo()
        readComments()
        getPostImage()

        binding.postComment.setOnClickListener {
            if (binding.addComment.text.toString() == "")
                Toast.makeText(requireContext(), "Сперва напишите сообщение!", Toast.LENGTH_SHORT).show()
            else
                addComment()
        }
    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        val commentsMap =  HashMap<String, Any>()
        commentsMap["comment"] = binding.addComment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()

        binding.addComment.text.clear()
    }

    private fun commentsUserInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(binding.profileImageComments)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId).child("postImage")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val image = dataSnapshot.value.toString()
                    Glide.with(requireContext()).load(image).placeholder(R.drawable.profile).into(binding.postImageComment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun readComments() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                    commentList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(Comment::class.java)
                    commentList!!.add(comment!!)
                }
                commentList!!.reverse()

                commentsAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addNotification() {
        val notificationsRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(publisherId)

        val notificationsMap = HashMap<String, Any>()
        notificationsMap["userId"] = firebaseUser!!.uid
        notificationsMap["text"] = "комментарий: ${binding.addComment.text}"
        notificationsMap["postId"] = postId
        notificationsMap["isPost"] = true

        notificationsRef.push().setValue(notificationsMap)

    }
}