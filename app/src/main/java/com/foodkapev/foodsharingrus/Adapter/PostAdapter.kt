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

//        Picasso.get().load(post.getPostImage()).into(holder.postImage)

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }

//        if (post.getDescription() == "") {
//            holder.description.visibility = View.GONE
//        }
//        else {
//            holder.description.visibility = View.VISIBLE
//            holder.description.text = post.getDescription()
//        }

//        val types = arrayOf("Съедобное", "Несъедобное")
//        if (holder.spinner != null) {
//            val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, types)
//            holder.spinner.adapter = arrayAdapter
//
//            holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View,
//                    position: Int,
//                    id: Long
//                ) {
//                    Toast.makeText(mContext, "asdasds" + " " + types[position], Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
//        }

        holder.title.text = post.getTitle()

        if (post.getTime() != "")
            holder.time.text = post.getTime()
        else
            holder.time.text = ""

        holder.location.text = post.getLocation()

//        if (holder.type.checkedRadioButtonId == R.id.edible_goods_type) {
//            post.getType()
//        }

        publisherInfo(post.getPublisher())

//        isLikes(post.getPostId(), holder.likeBtn)
//        numberOfLikes(holder.likes, post.getPostId())
//        numberOfComments(holder.comments, post.getPostId())
//        checkSavedStatus(post.getPostId(), holder.saveBtn)

//        holder.likeBtn.setOnClickListener {
//            if (holder.likeBtn.tag == "Like") {
//                FirebaseDatabase.getInstance().reference.child("Likes")
//                    .child(post.getPostId())
//                    .child(firebaseUser!!.uid)
//                    .setValue(true)
//
//                addNotification(post.getPublisher(), post.getPostId())
//            }
//            else {
//                FirebaseDatabase.getInstance().reference.child("Likes")
//                    .child(post.getPostId())
//                    .child(firebaseUser!!.uid)
//                    .removeValue()
//
//                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
//            }
//        }

//        holder.likes.setOnClickListener {
//            val intent = Intent(mContext, ShowUsersActivity::class.java)
//            intent.putExtra("id", post.getPostId())
//            intent.putExtra("title", "Likes")
//            mContext.startActivity(intent)
//        }

//        holder.commentBtn.setOnClickListener {
//            val commentsIntent = Intent(mContext, CommentsActivity::class.java)
//            commentsIntent.putExtra("postId", post.getPostId())
//            commentsIntent.putExtra("publisherId", post.getPublisher())
//            mContext.startActivity(commentsIntent)
//        }

//        holder.comments.setOnClickListener {
//            val commentsIntent = Intent(mContext, CommentsActivity::class.java)
//            commentsIntent.putExtra("postId", post.getPostId())
//            commentsIntent.putExtra("publisherId", post.getPublisher())
//            mContext.startActivity(commentsIntent)
//        }

//        holder.saveBtn.setOnClickListener {
//            if (holder.saveBtn.tag == "Save") {
//                FirebaseDatabase.getInstance().reference.child("Saves")
//                    .child(firebaseUser!!.uid)
//                    .child(post.getPostId())
//                    .setValue(true)
//            }
//            else {
//                FirebaseDatabase.getInstance().reference.child("Saves")
//                    .child(firebaseUser!!.uid)
//                    .child(post.getPostId())
//                    .removeValue()
//            }
//        }

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

//        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home)
//        var likeBtn: ImageView = itemView.findViewById(R.id.post_image_like_btn)
//        var commentBtn: ImageView = itemView.findViewById(R.id.post_image_comment_btn)
//        var saveBtn: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
//        var userName: TextView = itemView.findViewById(R.id.user_name_post)
//        var publisher: TextView = itemView.findViewById(R.id.publisher)
//        var likes: TextView = itemView.findViewById(R.id.likes)
//        var description: TextView = itemView.findViewById(R.id.description_post_home)
//        var comments: TextView = itemView.findViewById(R.id.comments)
        var title: TextView = itemView.findViewById(R.id.post_title_home)
        var time: TextView = itemView.findViewById(R.id.offer_time_home)
        var location: TextView = itemView.findViewById(R.id.post_location_home)
//        var type: RadioGroup = itemView.findViewById(R.id.goods_type_rg)
//        var profilePostTitle: TextView = itemView.findViewById(R.id.post_title)
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