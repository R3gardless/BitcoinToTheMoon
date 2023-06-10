package edu.skku.cs.bitcointothemoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import edu.skku.cs.bitcointothemoon.databinding.ActivityIntroBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// https://developer.android.com/develop/ui/views/launch/splash-screen
class IntroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.introFragmentContainerView.visibility = View.INVISIBLE

        CoroutineScope(Dispatchers.Main).launch{
            delay(3000)
            binding.tvIntroTitle.visibility = View.INVISIBLE
            binding.animationView1.visibility = View.INVISIBLE
            binding.introFragmentContainerView.visibility = View.VISIBLE

        }
    }
}