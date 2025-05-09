package com.nikidas.demo.githubviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nikidas.demo.githubviewer.databinding.ActivityMainBinding
import com.nikidas.demo.githubviewer.ui.profile.LoginActivity
import com.nikidas.demo.githubviewer.ui.profile.LoginViewModel
import com.nikidas.demo.githubviewer.util.LoginManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
        private const val LOGIN_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_popular, R.id.navigation_search, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 初始化时检查登录状态
        viewModel.checkLoginState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // 登录成功
                val user = LoginManager.getUser(this)
                Log.d(TAG, "Login successful: user = $user")
                if (user != null) {
                    Toast.makeText(this, "Login successful: ${user.username}", Toast.LENGTH_SHORT).show()
                    // 通知 ViewModel 更新登录状态
                    viewModel.checkLoginState()
                    Log.d(TAG, "Notified ViewModel to check login state")
                }
            } else {
                // 登录失败或取消
                Log.d(TAG, "Login failed or cancelled")
                Toast.makeText(this, "Login failed or cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}