package com.foodkapev.foodsharingrus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.Adapter.CommentsAdapter
import com.foodkapev.foodsharingrus.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentList: MutableList<com.foodkapev.foodsharingrus.Model.Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentsAdapter = CommentsAdapter(this, commentList)
        recyclerView.adapter = commentsAdapter

        commentsUserInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener {
            if (add_comment.text.toString() == "")
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
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()

        add_comment.text.clear()
    }

    private fun commentsUserInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (context != null)
//                    return

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comments)
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
//                if (context != null)
//                    return

                if (dataSnapshot.exists()) {
                    val image = dataSnapshot.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comment)

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
                    val comment = snapshot.getValue(com.foodkapev.foodsharingrus.Model.Comment::class.java)
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
        notificationsMap["text"] = "комментарий: ${add_comment.text}"
        notificationsMap["postId"] = postId
        notificationsMap["isPost"] = true

        notificationsRef.push().setValue(notificationsMap)

    }
}