package com.ivancic.explorengv3.activities

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.ConfigurationCompat
import com.google.firebase.storage.FirebaseStorage
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityDetailsBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.Marked
import java.util.*

class DetailsActivity : AppCompatActivity() {
    lateinit var binding:ActivityDetailsBinding
    lateinit var currentMarker:Marked
    private lateinit var locale: Locale
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)!!
        val position = intent.getIntExtra("pos",0)
        var a=getString(R.string.Detalji)
        currentMarker=Gallery.marker[position]
        val gsReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${currentMarker.upimage}/${currentMarker.image}.jpg")
        GlideApp.with(this).load(gsReference2).into(binding.photos)

        binding.returnToMenu.text=currentMarker.title

        binding.returnToMenu.setOnClickListener { startActivity(Intent(this@DetailsActivity, Gallery::class.java)) }

        a += if(locale.language=="hr") "\n\n ${currentMarker.detailedDesc}"
        else "\n\n ${currentMarker.detailedDescEng}"

        binding.description.text=a
    }
}