package com.ivancic.explorengv2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.ivancic.explorengv2.adapters.CustomAdapter
import com.ivancic.explorengv2.R
import com.ivancic.explorengv2.databinding.ActivityLeaderboardBinding
import com.ivancic.explorengv2.models.User4Leaderboard

class Leaderboard : AppCompatActivity() {
    lateinit var binding:ActivityLeaderboardBinding
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")

    var sortedList = ArrayList<User4Leaderboard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.returnToMenu.setOnClickListener {
            val intent= Intent(this, Menu::class.java)
            startActivity(intent) }

        database.addValueEventListener(object : ValueEventListener {
            var displayList = ArrayList<User4Leaderboard>()
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val a : List<User4Leaderboard> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                        User4Leaderboard::class.java)!! }

                    displayList.addAll(a)

                }catch (e : Exception){
                }
                while(displayList.isNotEmpty()) {
                    var max=0.0
                    var index=0
                    for ((i, v) in displayList.withIndex()) {
                        if (v.totalPoints>=max) {index=i
                        max=v.totalPoints}
                    }
                    sortedList.add(displayList[index])
                    displayList.removeAt(index)

                }

                for ((i, v) in sortedList.withIndex()) {
                    if (v.uid==Menu.currUser.uid) binding.myPosition.text= buildString {
        append(i+1)
        append(getString(R.string.mjesto))
    }
                }

                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = CustomAdapter(sortList(), this@Leaderboard)
                }
            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }

    private fun sortList(): ArrayList<User4Leaderboard> {

        return sortedList
    }
}