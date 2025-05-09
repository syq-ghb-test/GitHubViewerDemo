package com.nikidas.demo.githubviewer.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.nikidas.demo.githubviewer.R
import com.nikidas.demo.githubviewer.util.LoginManager

class ProfileFragment : Fragment() {
    private lateinit var layoutNotLoggedIn: LinearLayout
    private lateinit var layoutLoggedIn: LinearLayout
    private lateinit var btnLogin: Button
    private lateinit var ivAvatar: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var entryRepositories: LinearLayout
    private lateinit var btnLogout: Button
    private val viewModel: LoginViewModel by activityViewModels()

    companion object {
        private const val TAG = "ProfileFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        Log.d(TAG, "ViewModel initialized")
        
        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn)
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn)
        btnLogin = view.findViewById(R.id.btnLogin)
        ivAvatar = view.findViewById(R.id.ivAvatar)
        tvUsername = view.findViewById(R.id.tvUsername)
        entryRepositories = view.findViewById(R.id.entryRepositories)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogin.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            LoginActivity.startForResult(requireActivity(), 100)
        }

        btnLogout.setOnClickListener {
            Log.d(TAG, "Logout button clicked")
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout") { _, _ ->
                    viewModel.logout()
                }
                .show()
        }

        entryRepositories.setOnClickListener {
            val intent = Intent(requireContext(), com.nikidas.demo.githubviewer.ui.repositories.RepositoriesActivity::class.java)
            startActivity(intent)
        }

        // 观察登录状态变化
        viewModel.loginState.observe(viewLifecycleOwner) { user ->
            Log.d(TAG, "Login state changed: user = $user")
            updateUi(user)
        }

        // 立即检查当前登录状态
        viewModel.checkLoginState()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }

    private fun updateUi(user: LoginManager.User?) {
        Log.d(TAG, "Updating UI: user = $user")
        if (user != null) {
            // 已登录状态
            layoutNotLoggedIn.visibility = View.GONE
            layoutLoggedIn.visibility = View.VISIBLE
            
            // 更新用户信息
            tvUsername.text = user.username
            Glide.with(this)
                .load(user.avatarUrl)
                .circleCrop()
                .into(ivAvatar)
        } else {
            // 未登录状态
            layoutNotLoggedIn.visibility = View.VISIBLE
            layoutLoggedIn.visibility = View.GONE
        }
    }
}