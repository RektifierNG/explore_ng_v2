package com.ivancic.explorengv3.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityProfilBinding
import com.ivancic.explorengv3.login.LoginActivity
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.User

class Profil : AppCompatActivity() {
    private val REQUEST_CODE = 100
    private var userId:String=""
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityProfilBinding
    lateinit var currUser: User
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
    private val database2: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
       //currUser=LoginActivity.getUser()

        val currentUser = auth.currentUser

        if(currentUser != null) {
            userId = currentUser.uid
            //triggerchange(currentUser.uid)
        }

        binding.upload.setOnClickListener { uploadImage() }
        binding.btnLogin.setOnClickListener {
            Firebase.auth.signOut()
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)}
        binding.btnMenu.setOnClickListener { val intent= Intent(this, Menu::class.java)
            startActivity(intent) }

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
                        currUser=v
                        binding.welcome.text=v.name
                        binding.statistika.text=StringBuilder().
                        append(getString(com.ivancic.explorengv3.R.string.location_collection)).append("\n").append(v.collectedLocationNumber).
                        append("\n\n").append(getString(R.string.quizes)).append("\n").append(v.numberOfQuizzes).append("\n\n").
                        append(getString(R.string.quizes2)).append("\n").append(v.avgPointsQuiz).append("\n\n").
                        append(getString(R.string.last)).append("\n").append(v.lastCollectedLocation).append("\n\n").
                        append(getString(R.string.total)).append("\n").append(v.totalPoints).toString()
                        if (v.pic==0){
                            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/profile.jpg")
                            GlideApp.with(this@Profil).load(sRef).into(binding.userProfilePhoto)
                        }
                        else {
                            val sRef: StorageReference =
                                FirebaseStorage.getInstance().reference.child("users/ $userId/profile${v.pic}.jpg")
                            GlideApp.with(this@Profil).load(sRef).into(binding.userProfilePhoto)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val delete=currUser.pic
        val value=currUser.pic+1
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "users/ $userId/profile$value.jpg"
            )
            val uri: Uri = data!!.data!!
           val uploadTask= sRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                database.child(userId).child("1").child("pic").setValue(value)
                database2.child(Menu.currUser.userNo.toString()).child("pic").setValue(value)
                GlideApp.with(this@Profil).load(sRef).into(binding.userProfilePhoto)
                val deleteRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    "users/ $userId/profile$delete.jpg")
                deleteRef.delete()

            }

        }
    }
/*
    fun triggerchange(uid: String) {
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
                    if(v.uid==userId) {
                        currUser=v
                        binding.welcome.text=v.name
                        binding.statistika.text=StringBuilder().
                        append(getString(R.string.location_collection)).append("\n").append(v.collectedLocationNumber).
                        append("\n\n").append(getString(R.string.quizes)).append("\n").append(v.numberOfQuizzes).append("\n\n").
                        append(getString(R.string.quizes)).append("\n").append(v.avgPointsQuiz).append("\n\n").
                        append(getString(R.string.last)).append("\n").append(v.lastCollectedLocation).append("\n\n").
                        append(getString(R.string.total)).append("\n").append(v.totalPoints).toString()
                        if (v.pic==0){
                            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/profile.jpg")
                            GlideApp.with(this@Profil).load(sRef).into(binding.userProfilePhoto)
                        }
                        else {
                            val sRef: StorageReference =
                                FirebaseStorage.getInstance().reference.child("users/ $userId/profile${v.pic}.jpg")
                            GlideApp.with(this@Profil).load(sRef).into(binding.userProfilePhoto)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }*/
}