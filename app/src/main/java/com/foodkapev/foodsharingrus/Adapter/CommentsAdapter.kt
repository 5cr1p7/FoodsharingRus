package com.foodkapev.foodsharingrus.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.Model.Comment
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

class CommentsAdapter (private val mContext: Context,
                       private val mComment: MutableList<Comment>?)
: RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment!![position]
        holder.commentTextView.text = comment.getComment()

        getUserInfo(holder.imageProfile, holder.userNameTextView, comment.getPublisher())
    }

    private fun getUserInfo(
        imageProfile: CircleImageView,
        userNameTextView: TextView,
        publisher: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(publisher)

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)
                    userNameTextView.text = user.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageProfile: CircleImageView = itemView.findViewById(R.id.user_profile_image_comments)
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_comments)
        var commentTextView: TextView = itemView.findViewById(R.id.comment_comments_page)

    }
}
