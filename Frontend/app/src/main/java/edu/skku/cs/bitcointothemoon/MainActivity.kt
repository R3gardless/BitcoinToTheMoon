package edu.skku.cs.bitcointothemoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import edu.skku.cs.bitcointothemoon.Fragment.*
import edu.skku.cs.bitcointothemoon.ViewModel.UserViewModel
import edu.skku.cs.bitcointothemoon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel : UserViewModel by viewModels()
    var username : String? = null
    var nickname : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        username = intent.getStringExtra(IntroFragment.EXT_USERNAME)
        nickname = intent.getStringExtra(IntroFragment.EXT_NICKNAME)
        viewModel.initUser(username!!, nickname!!)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bottomNavigation = binding.bottomNavigation
        val navController = findNavController(R.id.mainFragementContainerView)

        bottomNavigation.setupWithNavController(navController)
    }
}