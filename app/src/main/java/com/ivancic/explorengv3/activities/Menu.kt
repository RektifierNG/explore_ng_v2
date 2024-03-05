package com.ivancic.explorengv3.activities

import android.animation.Animator
import android.content.Intent
import android.graphics.drawable.Drawable
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
        var imageList= ArrayList <Drawable>()
        imageList.clear()
        imageList.add(getDrawable(R.drawable.quiz)!!)
        imageList.add(getDrawable(R.drawable.explore)!!)
        imageList.add(getDrawable(R.drawable.edit_profile)!!)
        imageList.add(getDrawable(R.drawable.leaderboard)!!)
        imageList.add(getDrawable(R.drawable.gallery)!!)
        imageList.add(getDrawable(R.drawable.reward)!!)
        imageList.add(getDrawable(R.drawable.about_app)!!)
        imageList.add(getDrawable(R.drawable.about_project)!!)

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
                var offset: Int = binding.rV.computeHorizontalScrollOffset()



                offset=(offset*10)/(binding.rV.height-20)
                //var position: Int = offset / (binding.rV.height-20)
                //binding.offset.text=offset.toString()
              //  binding.offset.visibility= View.GONE

                if(offset in 130..140) {//provjereno ovo se prvo pokreće
                    position=7
                    binding.offset.text="about_project"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.o_projektu_left)
                    binding.title1.text=getString(R.string.o_projektu_right)
                    binding.title2.text=getString(R.string.o_projektu)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 150..160) { //position 8
                    position=0
                    binding.offset.text="quiz"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.kviz_left)
                    binding.title1.text=getString(R.string.kviz_right)
                    binding.title2.text=getString(R.string.kviz)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 170..180) {//position 9
                    position=1
                    binding.offset.text="explore"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.skupljanje_left)
                    binding.title1.text=getString(R.string.skupljanje_right)
                    binding.title2.text=getString(R.string.skupljanje)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 190..200) {//position 10
                    position=2
                    binding.offset.text="edit_profile"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.profil_left)
                    binding.title1.text=getString(R.string.profil_right)
                    binding.title2.text=getString(R.string.profil)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 210..220) {//position 11
                    position=3
                    binding.offset.text="leaderboard"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.poredak_left)
                    binding.title1.text=getString(R.string.poredak_right)
                    binding.title2.text=getString(R.string.poredak)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 70..80) { //position 4
                    position=4
                    binding.offset.text="gallery"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.galerija_left)
                    binding.title1.text=getString(R.string.galerija_right)
                    binding.title2.text=getString(R.string.galerija)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 90..100) { // position 5
                    position=5
                    binding.offset.text="reward"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.nagrade_left)
                    binding.title1.text=getString(R.string.nagrade_right)
                    binding.title2.text=getString(R.string.nagrade)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else if(offset in 110..120) {
                    position=6
                    binding.offset.text="about_app"
                    GlideApp.with(this@Menu).load(imageList[position]).into(binding.image)
                    binding.title.text=getString(R.string.about_left)
                    binding.title1.text=getString(R.string.about_right)
                    binding.title2.text=getString(R.string.about)
                    binding.title.animate().translationX(0f).duration = 1000
                    binding.title1.animate().translationX(0f).duration = 1000
                    binding.title2.animate().translationX(0f).duration = 1000
                    binding.frame.animate().scaleX(1f).scaleY(1f).duration = 1000
                }
                else{
                    binding.frame.animate().scaleX(0f).scaleY(0f).duration = 1000
                    binding.title.animate().translationXBy(-450F).setDuration(1000).withEndAction { binding.title.text=""
                        binding.title1.text="" }
                    binding.title1.animate().translationXBy(450F).duration = 1000
                    binding.title2.animate().translationXBy(450f).duration = 1000

                }

               if (offset>=240) binding.rV.scrollToPosition(4)
            if (offset<=55) binding.rV.scrollToPosition(11)

            }
        })
        binding.rV.scrollToPosition(7)

        binding.image.setOnClickListener {
            val lI=LinearInterpolator()
            when(position){
                0->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, QuizActivity::class.java)) }}
                1->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, MainActivity::class.java)) }}
                2->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Profil::class.java)) }}
                3->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Leaderboard::class.java)) }}
                4->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Gallery::class.java)) }}
                5->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, Reward::class.java)) }}
                6->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, About::class.java).apply {
                    putExtra("which", 0) }) }}
                7->{binding.frame.animate().scaleX(5f).scaleY(5f).setInterpolator(lI).setDuration(150).withEndAction { startActivity(Intent(this@Menu, About::class.java).apply {
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

    override fun onResume() {
        super.onResume()
        binding.frame.animate().scaleX(0f).scaleY(0f).duration = 1000
    }

    override fun onRestart() {
        super.onRestart()
        binding.frame.animate().scaleX(0f).scaleY(0f).duration = 1000
    }
}
