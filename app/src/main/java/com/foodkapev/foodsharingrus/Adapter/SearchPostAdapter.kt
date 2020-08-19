package com.foodkapev.foodsharingrus.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.Fragments.PostDetailsFragment
import com.foodkapev.foodsharingrus.MainActivity
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class SearchPostAdapter(private var mContext: Context,
                        private var mUser: List<Post>,
                        private var isFragment: Boolean = false): RecyclerView.Adapter<SearchPostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPostAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return SearchPostAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchPostAdapter.ViewHolder, position: Int) {
        val post = mUser[position]
        holder.title.text = post.getTitle()
        holder.time.text = post.getTime()
        holder.location.text = post.getLocation()
//        holder.description.text = post.getDescription()
        Picasso.get().load(post.getPostImage()).into(holder.postImage)

//        checkFollowingStatus(user.getUid(), holder.followBtn)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (isFragment) {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("postId", post.getPostId())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailsFragment()).commit()
            }
            else {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", post.getPostId())
                mContext.startActivity(intent)
            }
        })

//        holder.followBtn.setOnClickListener {
//            if (holder.followBtn.text.toString() == "Follow") {
//                firebaseUser?.uid.let { it ->
//                    FirebaseDatabase.getInstance().reference
//                            .child("Follow").child(it.toString())
//                            .child("Following").child(user.getUid())
//                            .setValue(true).addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    firebaseUser?.uid.let { it ->
//                                        FirebaseDatabase.getInstance().reference
//                                                .child("Follow").child(user.getUid())
//                                                .child("Followers").child(it.toString())
//                                                .setValue(true).addOnCompleteListener { task ->
//                                                    if (task.isSuccessful) {
//
//                                                    }
//
//                                                }
//                                    }
//
//                                }
//
//                            }
//                }
//                addNotification(user.getUid())
//
//            } else {
//                firebaseUser?.uid.let { it ->
//                    FirebaseDatabase.getInstance().reference
//                            .child("Follow").child(it.toString())
//                            .child("Following").child(user.getUid())
//                            .removeValue().addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    firebaseUser?.uid.let { it ->
//                                        FirebaseDatabase.getInstance().reference
//                                                .child("Follow").child(user.getUid())
//                                                .child("Followers").child(it.toString())
//                                                .removeValue().addOnCompleteListener { task ->
//                                                    if (task.isSuccessful) {
//
//                                                    }
//
//                                                }
//                                    }
//
//                                }
//
//                            }
//                }
//            }
//
//        }
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home)
        var title: TextView = itemView.findViewById(R.id.post_title_home)
        var time: TextView = itemView.findViewById(R.id.offer_time_home)
        var location: TextView = itemView.findViewById(R.id.post_location_home)
//        var description: TextView = itemView.findViewById(R.id.description_post_home)
    }

//    private fun checkFollowingStatus(uid: String, followBtn: Button) {
//        val followingRef = firebaseUser?.uid.let { it ->
//            FirebaseDatabase.getInstance().reference
//                    .child("Follow").child(it.toString())
//                    .child("Following")
//        }
//
//        followingRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(datasnapshot: DataSnapshot) {
//                if (datasnapshot.child(uid).exists())
//                    followBtn.text = "Following"
//                else
//                    followBtn.text = "Follow"
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//
//    }

//    private fun addNotification(userId: String) {
//        val notificationsRef = FirebaseDatabase.getInstance().reference
//            .child("Notifications")
//            .child(userId)
//
//        val notificationsMap = HashMap<String, Any>()
//        notificationsMap["userId"] = firebaseUser!!.uid
//        notificationsMap["text"] = "started following you"
//        notificationsMap["postId"] = ""
//        notificationsMap["isPost"] = false
//
//        notificationsRef.push().setValue(notificationsMap)
//
//    }
}