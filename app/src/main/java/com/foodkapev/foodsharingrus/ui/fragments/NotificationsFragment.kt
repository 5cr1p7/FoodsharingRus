package com.foodkapev.foodsharingrus.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.domain.models.Notification
import com.foodkapev.foodsharingrus.ui.adapters.NotificationsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private var notificationsList: List<Notification>? = null
    private var notificationsAdapter: NotificationsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        notificationsList = ArrayList()

        notificationsAdapter = NotificationsAdapter(requireContext(), notificationsList as ArrayList<Notification>)
        recyclerView.adapter = notificationsAdapter

        readNotifications()
    }

    private fun readNotifications() {
        val notificationsRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notificationsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (notificationsList as ArrayList<Notification>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val notification = snapshot.getValue(Notification::class.java)

                        (notificationsList as ArrayList<Notification>).add(notification!!)
                    }

                    Collections.reverse(notificationsList)
                    notificationsAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}