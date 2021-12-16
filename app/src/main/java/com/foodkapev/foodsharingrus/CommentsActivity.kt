package com.foodkapev.foodsharingrus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.presentation.adapters.CommentsAdapter
import com.foodkapev.foodsharingrus.domain.models.User
import com.foodkapev.foodsharingrus.databinding.ActivityCommentsBinding
import com.foodkapev.foodsharingrus.domain.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null
    private val binding by viewBinding(ActivityCommentsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_comments)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentsAdapter = CommentsAdapter(this, commentList)
        recyclerView.adapter = commentsAdapter

        commentsUserInfo()
        readComments()
        getPostImage()

        binding.postComment.setOnClickListener {
            if (binding.addComment.text.toString() == "")
                Toast.makeText(this, "Сперва напишите сообщение!", Toast.LENGTH_SHORT).show()
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
                    Glide.with(applicationContext).load(image).placeholder(R.drawable.profile).into(binding.postImageComment)
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