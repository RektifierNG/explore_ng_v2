package com.ivancic.explorengv3.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.x.circlelayout.CircleLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.adapters.MenuAdapter
import com.ivancic.explorengv3.databinding.ActivityMenuNewUiBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.User


class Menu : AppCompatActivity() {
    private var userId:String=""

    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")

    lateinit var circularLayout: CircleLayout
    lateinit var binding: ActivityMenuNewUiBinding


    companion object {
        private val database: DatabaseReference =
            FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
        private var userId:String=""

        private lateinit var auth: FirebaseAuth


        fun triggerchange() {
            auth = FirebaseAuth.getInstance()
            //currUser=LoginActivity.getUser()

            val currentUser = auth.currentUser

            if(currentUser != null) {
                userId = currentUser.uid
            }
            database.addValueEventListener(object : ValueEventListener {
                var displayList = ArrayList<User>()
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        val a : List<User> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                            User::class.java)!! }

                        displayList.addAll(a)

                    }catch (e : Exception){
                    }

                    for (v in displayList) {
                        if(v.uid==userId) currUser =v
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        lateinit var currUser: User
        var position: Int= 0

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMenuNewUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //currUser=LoginActivity.getUser()
        var imageList= ArrayList <Int>()
        imageList.add(R.drawable.quiz)
        imageList.add(R.drawable.edit_profile)
        imageList.add(R.drawable.leaderboard)
        imageList.add(R.drawable.gallery)
        imageList.add(R.drawable.reward)
        imageList.add(R.drawable.about_app)
        imageList.add(R.drawable.about_project)

        binding.frame.animate().scaleX(0f).scaleY(0f).duration = 1000
        var rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.fillAfter = true;
        binding.pos.startAnimation(rotation);


        binding.rV.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            adapter = MenuAdapter(imageList, this@Menu) { position ->
                Toast.makeText(this@Menu,position.toString(),Toast.LENGTH_SHORT).show()
            }
        }


        binding.rV.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset: Int = binding.rV.computeHorizontalScrollOffset()


                binding.offset.visibility= View.GONE

                //var position: Int = offset / (binding.rV.height-20)


                if(offset in 1950..2100) {
                    position=0
                    GlideApp.with(this@Menu).load(R.drawable.quiz).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 2250..2400) {
                    position=1
                    GlideApp.with(this@Menu).load(R.drawable.edit_profile).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 2550..2700) {
                    position=2
                    GlideApp.with(this@Menu).load(R.drawable.leaderboard).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 2850..3000) {
                    position=3
                    GlideApp.with(this@Menu).load(R.drawable.gallery).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 1000..1150) {
                    position=4
                    GlideApp.with(this@Menu).load(R.drawable.reward).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 1300..1450) {
                    position=5
                    GlideApp.with(this@Menu).load(R.drawable.about_app).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 1600..1750) {
                    position=6
                    GlideApp.with(this@Menu).load(R.drawable.about_project).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else binding.frame.animate().scaleX(0f).scaleY(0f).duration = 1000

               if (offset>=3150) binding.rV.scrollToPosition(3)
                if (offset<=880) binding.rV.scrollToPosition(11)

            }
        })
        binding.rV.scrollToPosition(7)

        binding.image.setOnClickListener {
            val lI=LinearInterpolator()
            when(position){
                0->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, QuizActivity::class.java)) }}
                1->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Profil::class.java)) }}
                2->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Leaderboard::class.java)) }}
                3->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Gallery::class.java)) }}
                4->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Reward::class.java)) }}
                5->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, About::class.java).apply {
                    putExtra("which", 0) }) }}
                6->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, About::class.java).apply {
                    putExtra("which", 1) }) }}

            }
        }

        val currentUser = auth.currentUser

        if(currentUser != null) {
            userId = currentUser.uid
        }

        database.child(userId).addValueEventListener(object : ValueEventListener {
            var displayList = ArrayList<User>()
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val a : List<User> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                        User::class.java)!! }

                    displayList.addAll(a)

                }catch (e : Exception){
                }
                for (v in displayList) {
                    if(v.uid==userId) {
                        //binding.welcome.text=v.name
                        currUser =v
                        if (v.pic==0){
                            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/profile.jpg")
                           // GlideApp.with(this@Menu).load(sRef).into(binding.userProfilePhoto)
                        }
                        else {
                            val sRef: StorageReference =
                                FirebaseStorage.getInstance().reference.child("users/ $userId/profile${v.pic}.jpg")
                            //GlideApp.with(this@Menu).load(sRef).into(binding.userProfilePhoto)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

}