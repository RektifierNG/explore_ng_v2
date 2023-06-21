package com.ivancic.explorengv3.activities

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.ivancic.explorengv3.adapters.CustomAdapter
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityLeaderboardBinding
import com.ivancic.explorengv3.models.User4Leaderboard
import java.util.Locale

class Leaderboard : AppCompatActivity() {
    lateinit var binding:ActivityLeaderboardBinding
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")
    private lateinit var locale: Locale
    var sortedList = ArrayList<User4Leaderboard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)!!
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
                    if(!sortedList.contains(displayList[index]))
                        sortedList.add(displayList[index])
                    displayList.removeAt(index)

                }

                for ((i, v) in sortedList.withIndex()) {
                    if (v.uid==Menu.currUser.uid)
                    {
                        if(locale.language=="hr"){
                        binding.myPosition.text= buildString {
                        append(i + 1)
                        append(getString(R.string.mjesto))}}
                            else binding.myPosition.text = buildString {
                                    append(i + 1)
                                    append(
                                        when ((i + 1) % 10) {
                                            1 -> "st "
                                            2 -> "nd "
                                            3 -> "rd "
                                            else -> "th "
                                        }
                                    )
                                    append(getString(R.string.mjesto))
                                }
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