package com.foodkapev.foodsharingrus

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.foodkapev.foodsharingrus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

//        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        binding.bottomNavView.setupWithNavController(navController)


        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        binding.bottomNavView.selectedItemId = R.id.nav_home
//        moveToFragment(HomeFragment())
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        when(item.itemId) {
            R.id.nav_search -> {
                navController.navigate(R.id.searchFragment)
//                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
//                startActivity(Intent(this, AddPostActivity::class.java))
                navController.navigate(R.id.addPostActivity)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_home -> {
                navController.navigate(R.id.homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                navController.navigate(R.id.notificationsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
//                moveToFragment(ProfileFragment())
                navController.navigate(R.id.profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}