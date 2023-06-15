package com.ivancic.explorengv3.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ivancic.explorengv3.databinding.Activity360Binding

class Activity360 : AppCompatActivity() {
    lateinit var binding:Activity360Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=Activity360Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}