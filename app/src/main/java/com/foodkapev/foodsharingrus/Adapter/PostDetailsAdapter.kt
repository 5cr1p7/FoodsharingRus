package com.foodkapev.foodsharingrus.Adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.CommentsActivity
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
import de.hdodenhof.circleimageview.CircleImageView

class PostDetailsAdapter(private val mContext: Context,
                         private val mPost: List<Post>): RecyclerView.Adapter<PostDetailsAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.particular_post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

//        Picasso.get().load(post.getPostImage()).into(holder.postImage)
        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage)

        if (post.getDescription() == "") {
            holder.description.visibility = View.GONE
        }
        else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        holder.title.text = post.getTitle()

//        holder.time.text = post.getTime()

        publisherInfo(holder.profileImage, holder.userName, post.getPublisher())

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

        holder.commentBtn.setOnClickListener {
            val commentsIntent = Intent(mContext, CommentsActivity::class.java)
            commentsIntent.putExtra("postId", post.getPostId())
            commentsIntent.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(commentsIntent)
        }

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

//    private fun numberOfLikes(likes: TextView, postId: String) {
//        val likesRef = FirebaseDatabase.getInstance().reference
//            .child("Likes")
//            .child(postId)
//
//        likesRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    likes.text = dataSnapshot.childrenCount.toString() + " likes"
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

    private fun numberOfComments(comments: TextView, postId: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    comments.text = "Посмотреть все " + dataSnapshot.childrenCount.toString() + " комментариев"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

//    private fun isLikes(postId: String, likeBtn: ImageView) {
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//
//        val likesRef = FirebaseDatabase.getInstance().reference
//            .child("Likes")
//            .child(postId)
//
//        likesRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
//                    likeBtn.setImageResource(R.drawable.heart_clicked)
//                    likeBtn.tag = "Liked"
//                }
//                else {
//                    likeBtn.setImageResource(R.drawable.heart)
//                    likeBtn.tag = "Like"
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {

        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post_details)
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home_details)
//        var likeBtn: ImageView = itemView.findViewById(R.id.post_image_like_btn_details)
        var commentBtn: ImageView = itemView.findViewById(R.id.post_image_comment_btn_details)
//        var saveBtn: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
        var userName: TextView = itemView.findViewById(R.id.user_name_post_details)
//        var publisher: TextView = itemView.findViewById(R.id.publisher)
//        var likes: TextView = itemView.findViewById(R.id.likes)
        var description: TextView = itemView.findViewById(R.id.description)
//        var comments: TextView = itemView.findViewById(R.id.comments)
        var title: TextView = itemView.findViewById(R.id.title_post_details)
//        var location: TextView = itemView.findViewById(R.id.title_post_details)
//        var time: TextView = itemView.findViewById(R.id.offer_time_home)
//        var profilePostTitle: TextView = itemView.findViewById(R.id.post_title)
    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisherId: String
    ) {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(publisherId)

        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user.getUsername()
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
