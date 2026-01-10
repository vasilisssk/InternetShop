package com.game.internetshop.ui.main

import android.os.Bundle
import android.util.Log
import com.game.internetshop.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.game.internetshop.databinding.ActivityMainBinding
import com.game.internetshop.ui.customBottomNavigationView.IconSource.Companion.resource
import com.game.internetshop.ui.customBottomNavigationView.setup

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setup {
            sections {
                section {
                    title("Catalogue")
                    iconSource(resource(R.drawable.ic_catalogue))
                    link("catalogue")
                }
                section {
                    title("Cart")
                    iconSource(resource(R.drawable.ic_cart))
                    link("cart")
                }
                section {
                    title("Orders")
                    iconSource(resource(R.drawable.ic_orders))
                    link("orders")
                }
                section {
                    title("Settings")
                    iconSource(resource(R.drawable.ic_settings))
                    link("settings")
                }
            }

            onItemClicked { section ->
                navController.navigate(route = section.link)
            }
        }
    }
}