package com.foodkapev.foodsharingrus.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.Fragments.PostDetailsFragment

class ImagesAdapter(private val mContext: Context, private val mPost: List<Post>)
    : RecyclerView.Adapter<ImagesAdapter.ViewHolder?>() {

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        var postTitle: TextView = itemView.findViewById(R.id.post_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost[position]
//        Picasso.get().load(post.getPostImage()).into(holder.postImage)
        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage)
        holder.postTitle.text = post.getTitle()

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }
    }

}