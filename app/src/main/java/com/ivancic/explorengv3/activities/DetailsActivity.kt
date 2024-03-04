package com.ivancic.explorengv3.activities

import android.animation.Animator
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation.AnimationListener
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.core.animate
import androidx.core.os.ConfigurationCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityDetailsBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.Marked
import java.util.*

class DetailsActivity : AppCompatActivity() {


    private lateinit var locale: Locale
    private var clicked:Boolean=false
    private var doubleClickedLastTime=0L

    companion object {
        lateinit var currentMarker:Marked
        lateinit var binding:ActivityDetailsBinding
        var flinged = false
        var i=0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)!!
        val position = intent.getIntExtra("pos",0)
        var a=getString(R.string.Detalji)
        currentMarker=Gallery.marker[position]
        var gsReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${currentMarker.upimage}/${currentMarker.image}.jpg")
        GlideApp.with(this).load(gsReference2).into(binding.photos)
        GlideApp.with(this).load(gsReference2).into(binding.photos2)


        if(i==0) Glide.with(this).load(R.drawable.ic_baseline_arrow_back_24_g).into(binding.back)
        binding.forward.setOnClickListener {
            if(i==currentMarker.noImages!!.toInt()) Glide.with(this).load(R.drawable.ic_baseline_arrow_forward_24_g).into(binding.forward)
            else {
                Glide.with(this).load(R.drawable.ic_baseline_arrow_back_24).into(binding.back)
                i++
                gsReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${currentMarker.upimage}/${currentMarker.image}$i.jpg")
                GlideApp.with(this).load(gsReference2).into(binding.photos)
                GlideApp.with(this).load(gsReference2).into(binding.photos2)
                if(i==currentMarker.noImages!!.toInt()) Glide.with(this).load(R.drawable.ic_baseline_arrow_forward_24_g).into(binding.forward)
            }
        }
        binding.back.setOnClickListener {
            if(i==1) Glide.with(this).load(R.drawable.ic_baseline_arrow_back_24_g).into(binding.back)
            else {
                Glide.with(this).load(R.drawable.ic_baseline_arrow_forward_24).into(binding.forward)
                i--
                gsReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${currentMarker.upimage}/${currentMarker.image}$i.jpg")
                GlideApp.with(this).load(gsReference2).into(binding.photos)
                GlideApp.with(this).load(gsReference2).into(binding.photos2)
            }
        }


        binding.photos.setOnClickListener{
            binding.photos2.visibility= View.VISIBLE
            binding.photos2.animate().alpha(1f).setDuration(1500)
        clicked=true}



        binding.photos2.setOnClickListener{


            if(System.currentTimeMillis()-doubleClickedLastTime<300){
                doubleClickedLastTime=0
                binding.photos2.animate().alpha(0f)
                    .setDuration(1500)
                    .withEndAction { binding.photos2.visibility=View.GONE }
            }else{
                doubleClickedLastTime=System.currentTimeMillis()
            }}



        binding.returnToMenu.text=currentMarker.title

        binding.returnToMenu.setOnClickListener { startActivity(Intent(this@DetailsActivity, Gallery::class.java)) }

        a += if(locale.language=="hr") "\n\n ${currentMarker.detailedDesc}"
        else "\n\n ${currentMarker.detailedDescEng}"

        binding.description.text=a
    }

    private fun hide() {
        TODO("Not yet implemented")
    }

}