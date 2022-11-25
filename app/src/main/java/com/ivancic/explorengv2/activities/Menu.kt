package com.ivancic.explorengv2.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv2.databinding.ActivityMenuBinding
import com.ivancic.explorengv2.models.GlideApp
import com.ivancic.explorengv2.models.User

class Menu : AppCompatActivity() {
    private var userId:String=""

    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")

    lateinit var binding: ActivityMenuBinding
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

                    for ((i, v) in displayList.withIndex()) {
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
        binding=ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //currUser=LoginActivity.getUser()

        val currentUser = auth.currentUser

        if(currentUser != null) {
            userId = currentUser.uid
        }

        binding.btnPoredak.setOnClickListener {
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
                        binding.welcome.text=v.name
                        currUser =v
                        if (v.pic==0){
                            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/profile.jpg")
                            GlideApp.with(this@Menu).load(sRef).into(binding.userProfilePhoto)
                        }
                        else {
                            val sRef: StorageReference =
                                FirebaseStorage.getInstance().reference.child("users/ $userId/profile${v.pic}.jpg")
                            GlideApp.with(this@Menu).load(sRef).into(binding.userProfilePhoto)
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