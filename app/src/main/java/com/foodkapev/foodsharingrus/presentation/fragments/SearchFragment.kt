package com.foodkapev.foodsharingrus.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.presentation.adapters.SearchPostAdapter
import com.foodkapev.foodsharingrus.domain.models.Post
import com.foodkapev.foodsharingrus.databinding.FragmentSearchBinding
import com.foodkapev.foodsharingrus.presentation.viewmodels.SearchViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var postAdapter: SearchPostAdapter? = null
    private var mPost: MutableList<Post>? = null

    private val binding by viewBinding(FragmentSearchBinding::bind)

    val searchViewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewSearch.setHasFixedSize(true)
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(context, 2)

        mPost = ArrayList()
        postAdapter = context?.let { SearchPostAdapter(it, mPost as ArrayList<Post>, true) }
        binding.recyclerViewSearch.adapter = postAdapter

        binding.searchEditText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.text.toString() == "") {

                }
                else {
                    binding.recyclerViewSearch.visibility = View.VISIBLE

                    retrievePosts()
//                    searchPost(s.toString())
                    searchViewModel.searchPost(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

        })
    }

//    private fun searchPost(input: String) {
//        val query = FirebaseDatabase.getInstance().reference
//            .child("Posts")
//            .orderByChild("title")
//            .startAt(input)
//            .endAt(input + "\uf8ff")
//
//        query.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnaphot: DataSnapshot) {
//                mPost?.clear()
//                for (snapshot in dataSnaphot.children) {
//                    val post = snapshot.getValue(Post::class.java)
//                    if (post != null) {
//                        mPost?.add(post)
//                    }
//                }
//            }
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })
//    }

    private fun retrievePosts() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnaphot: DataSnapshot) {
//                searchViewModel.searchText.value = binding.searchEditText.text.toString()
                    if (binding.searchEditText.text.toString() == "") {
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