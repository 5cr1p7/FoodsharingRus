package com.foodkapev.foodsharingrus.presentation.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodkapev.foodsharingrus.domain.models.Post
import com.foodkapev.foodsharingrus.R

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

        Glide.with(mContext).load(post.postImage).into(holder.postImage)
        holder.postTitle.text = post.title

        Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_postDetailsFragment)

        holder.postImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("postId", post.postId)
            Navigation.findNavController(holder.itemView).navigate(R.id.action_profileFragment_to_postDetailsFragment, bundle)
        }
    }

}