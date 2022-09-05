package com.foodkapev.foodsharingrus.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.R
import com.foodkapev.foodsharingrus.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private var navHostFragment: NavHostFragment? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.nav_graph)
        if (firebaseUser != null) {
            graph?.setStartDestination(R.id.homeFragment)
        }
        if (graph != null) {
            navHostFragment?.navController?.graph = graph
        }

        binding.bottomNavView.setOnItemSelectedListener(onNavigationItemSelectedListener)

    }

    private val onNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment?.navController
            when (item.itemId) {
                R.id.nav_search -> {
                    navController?.navigate(R.id.action_global_searchFragment)
                    return@OnItemSelectedListener true
                }
                R.id.nav_add_post -> {
                    navController?.navigate(R.id.action_global_addPostFragment)
                    return@OnItemSelectedListener true
                }
                R.id.nav_home -> {
                    navController?.navigate(R.id.action_global_homeFragment)
                    return@OnItemSelectedListener true
                }
                R.id.nav_notifications -> {
                    navController?.navigate(R.id.action_global_notificationsFragment)
                    return@OnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    navController?.navigate(R.id.action_global_profileFragment)
                    return@OnItemSelectedListener true
                }
            }
            false
        }
}