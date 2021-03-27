package com.foodkapev.foodsharingrus.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.Fragments.PostDetailsFragment
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.Model.User
import com.foodkapev.foodsharingrus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.HashMap

class PostAdapter(private val mContext: Context,
                  private val mPost: List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage)

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }



        holder.title.text = post.getTitle()

        if (post.getTime() != "")
            holder.time.text = post.getTime()
        else
            holder.time.text = ""

        holder.location.text = post.getLocation()

        publisherInfo(post.getPublisher())

    }

    private fun numberOfLikes(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postId)

        likesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    likes.text = dataSnapshot.childrenCount.toString() + " likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun numberOfComments(comments: TextView, postId: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    comments.text = "View all " + dataSnapshot.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun isLikes(postId: String, likeBtn: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postId)

        likesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    likeBtn.setImageResource(R.drawable.heart_clicked)
                    likeBtn.tag = "Liked"
                }
                else {
                    likeBtn.setImageResource(R.drawable.heart)
                    likeBtn.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home)
        var title: TextView = itemView.findViewById(R.id.post_title_home)
        var time: TextView = itemView.findViewById(R.id.offer_time_home)
        var location: TextView = itemView.findViewById(R.id.post_location_home)
    }

    private fun publisherInfo(
        publisherId: String
    ) {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(publisherId)

        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

//                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
//                    userName.text = user.getUsername()
//                    publisher.text = user.getFullname()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun checkSavedStatus(postId: String, imageView: ImageView) {
        val savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag = "Saved"
                }
                else {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag = "Save"
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addNotification(userId: String, postId: String) {
        val notificationsRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(userId)

        val notificationsMap = HashMap<String, Any>()
        notificationsMap["userId"] = firebaseUser!!.uid
        notificationsMap["text"] = "liked your post"
        notificationsMap["postId"] = postId
        notificationsMap["isPost"] = true

        notificationsRef.push().setValue(notificationsMap)

    }

}