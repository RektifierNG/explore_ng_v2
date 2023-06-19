package com.ivancic.explorengv3.activities

import android.os.Bundle
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
                /* 0- <2570, 2950>
                *  1    <2960,3390>
                   2     <3400, 3770>
                    3    * 3780, 4190
                    4    * 4200, 4600   || 1320, 1730
                    5    * 2100, 1740
                    *6       2110, 2560

                * */


                var position: Int = offset / (binding.rV.height-20)
                if(position==14) binding.rV.scrollToPosition(4)
                if(position==3) binding.rV.scrollToPosition(11)

                if(offset in 2650..2900) {
                    position=0
                    GlideApp.with(this@Menu).load(R.drawable.quiz).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 3100..3300) {
                    position=1
                    GlideApp.with(this@Menu).load(R.drawable.edit_profile).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 3500..3700) {
                    position=2
                    GlideApp.with(this@Menu).load(R.drawable.leaderboard).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 3900..4100) {
                    position=3
                    GlideApp.with(this@Menu).load(R.drawable.gallery).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 4300..4500 ||offset in 1400..1650) {
                    position=4
                    GlideApp.with(this@Menu).load(R.drawable.reward).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 1850..2000) {
                    position=5
                    GlideApp.with(this@Menu).load(R.drawable.about_app).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else if(offset in 2200..2450) {
                    position=6
                    GlideApp.with(this@Menu).load(R.drawable.about_project).into(binding.image)
                    binding.frame.animate().scaleX(1f).scaleY(1f).setDuration(1000)
                }
                else binding.frame.animate().scaleX(0f).scaleY(0f).setDuration(1000)

            }
        })
        binding.rV.scrollToPosition(7)

       /* ad.add(R.drawable.quiz)
        ad.add(R.drawable.edit_profile)
        ad.add(R.drawable.gallery)
        ad.add(R.drawable.leaderboard)
        ad.add(R.drawable.reward)
        circularLayout.setAdapter(ad)
        circularLayout.setChildrenCount(4)
        circularLayout.setRadius(120)
        circularLayout.setChildrenPinned(true)*/
        val currentUser = auth.currentUser

        if(currentUser != null) {
            userId = currentUser.uid
        }

      /*  binding.btnPoredak.setOnClickListener {
            val intent=Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }


        binding.btnCollect.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent(this, MainActivity::class.java)
            } else {
                TODO("VERSION.SDK_INT < S")
            }
            intent.putExtra("uid", currUser.uid)
            startActivity(intent)
        }
        binding.btnProfil.setOnClickListener {
            val intent = Intent(this, Profil::class.java)
            startActivity(intent)
        }
        binding.btnKviz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("noQuizzes", currUser.numberOfQuizzes)
            intent.putExtra("quizPoints", currUser.totalQuizPoint)
            intent.putExtra("totalPoints", currUser.totalPoints)
            startActivity(intent)
        }
        binding.btnGalerija.setOnClickListener {
            val intent = Intent(this, Gallery::class.java)
            startActivity(intent)
        }
*/
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