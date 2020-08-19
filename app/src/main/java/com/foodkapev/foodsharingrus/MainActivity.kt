package com.foodkapev.foodsharingrus

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.foodkapev.foodsharingrus.Fragments.HomeFragment
import com.foodkapev.foodsharingrus.Fragments.NotificationsFragment
import com.foodkapev.foodsharingrus.Fragments.ProfileFragment
import com.foodkapev.foodsharingrus.Fragments.SearchFragment

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        val navController = findNavController(R.id.nav_host_fragment)
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        toolbar.setupWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.nav_home
        moveToFragment(HomeFragment())
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
//        fragmentTrans.addToBackStack(null)
//        fragmentTrans.commit()
    }
}