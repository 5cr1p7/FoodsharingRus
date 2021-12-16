package com.foodkapev.foodsharingrus.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.domain.models.Notification
import com.foodkapev.foodsharingrus.domain.models.Post
import com.foodkapev.foodsharingrus.domain.models.User
import com.foodkapev.foodsharingrus.presentation.fragments.NotificationsFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationsAdapter(
    private val mContext: Context,
    private val mNotification: List<Notification>)
    : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var postImage: ImageView = itemView.findViewById(R.id.notifications_post_image)
        var profileImage: CircleImageView = itemView.findViewById(R.id.notifications_profile_image)
        var userName: TextView = itemView.findViewById(R.id.username_notifications)
        var text: TextView = itemView.findViewById(R.id.comment_notifications)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotification[position]

        when {
            notification.text == "started following you" -> holder.text.text =
                "started following you"
            notification.text == "liked your post" -> holder.text.text = "liked your post"
            notification.text.contains("liked your post") -> holder.text.text =
                notification.text.replace("commented:", "Комментарий: ")
            else -> holder.text.text = notification.text
        }

        holder.itemView.setOnClickListener {
            if (notification.isPost) {
                val action = NotificationsFragmentDirections.actionNotificationsFragmentToPostDetailsFragment(notification.postId)
                Navigation.findNavController(it).navigate(action)
            } else {
                val action = NotificationsFragmentDirections.actionNotificationsFragmentToProfileFragment(notification.userId)
                Navigation.findNavController(it).navigate(action)
            }
        }
        userInfo(holder.profileImage, holder.userName, notification.userId)

        if (notification.isPost) {
            holder.postImage.visibility = View.VISIBLE
            getPostImage(holder.postImage, notification.postId)
        } else
            holder.postImage.visibility = View.GONE

    }

    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile)
                        .into(imageView)
                    userName.text = user.username
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getPostImage(imageView: ImageView, postId: String) {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    val post = dataSnapshot.getValue(Post::class.java)

                    Picasso.get().load(post!!.postImage).placeholder(R.drawable.profile)
                        .into(imageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}