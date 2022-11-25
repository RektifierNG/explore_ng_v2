package com.ivancic.explorengv2.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.ivancic.explorengv2.R
import com.ivancic.explorengv2.databinding.ActivityLauncherBinding
import com.ivancic.explorengv2.login.LoginActivity


class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            val video: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.logo)
            binding.video.setVideoURI(video)

            binding.video.setOnCompletionListener { jump() }
            binding.video.setOnClickListener { jump() }
            binding.video.start()
        }
     catch (ex: Exception) {
        jump()
    }

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        jump()
        return true}

    private fun jump() {
        if (isFinishing)
            return
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        finish()
}}