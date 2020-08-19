package com.foodkapev.foodsharingrus.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.Adapter.PostAdapter
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_home)
        recyclerView.setHasFixedSize(true)

        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        val types = arrayOf("Все предложения", "Съедобное", "Несъедобное")
        val spinner = view.findViewById<Spinner>(R.id.goods_type_spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item, types
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    when (types[position]) {
                        "Все предложения" -> retrieveAllPosts()
                        "Съедобное" -> retrieveEdiblePosts()
                        "Несъедобное" -> retrieveInediblePosts()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

        }

//        checkFollowings()

        return view
    }

//    private fun checkFollowings() {
//        followingList = ArrayList()
//
//        val followingRef = FirebaseDatabase.getInstance().reference
//            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
//            .child("Following")
//
//        followingRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    (followingList as ArrayList<String>).clear()
//
//                    for (snapshot in dataSnapshot.children) {
//                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
//                    }
//
//                    retrievePosts()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

//    private fun currentUserPosts() {
//        val postsRef = FirebaseDatabase.getInstance().reference
//            .child("Posts")
//
//        postsRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    (postList as ArrayList<Post>).clear()
//
//                    for (snapshot in dataSnapshot.children) {
//                        val post = snapshot.getValue(Post::class.java)
//                        (postList as ArrayList<Post>).add(post!!)
//                        Collections.reverse(postList)
//
//                        postAdapter!!.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }

    private fun retrieveAllPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        (postList as ArrayList<Post>).add(post!!)
                        Collections.reverse(postList)

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrieveEdiblePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.getType() == "Съедобное")
                            (postList as ArrayList<Post>).add(post)

                        Collections.reverse(postList)
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrieveInediblePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.getType() == "Несъедобное")
                            (postList as ArrayList<Post>).add(post)

                        Collections.reverse(postList)
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}