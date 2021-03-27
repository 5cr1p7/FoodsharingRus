package com.foodkapev.foodsharingrus.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.Adapter.SearchPostAdapter
import com.foodkapev.foodsharingrus.Model.Post
import com.foodkapev.foodsharingrus.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var postAdapter: SearchPostAdapter? = null
    private var mPost: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = GridLayoutManager(context, 2)

        mPost = ArrayList()
        postAdapter = context?.let { SearchPostAdapter(it, mPost as ArrayList<Post>, true) }
        recyclerView?.adapter = postAdapter

        view.search_edit_text.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.search_edit_text.text.toString() == "") {

                }
                else {
                    recyclerView?.visibility = View.VISIBLE

                    retrievePosts()
                    searchPost(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

        })

        return view
    }

    private fun searchPost(input: String) {
        val query = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .orderByChild("title")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnaphot: DataSnapshot) {
                mPost?.clear()
                for (snapshot in dataSnaphot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post != null) {
                        mPost?.add(post)
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun retrievePosts() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnaphot: DataSnapshot) {
                if (view?.search_edit_text?.text.toString() == "") {
                    mPost?.clear()

                    for (snapshot in dataSnaphot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post != null) {
                            mPost?.add(post)
                        }
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}