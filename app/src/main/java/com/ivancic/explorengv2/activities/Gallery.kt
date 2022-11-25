package com.ivancic.explorengv2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*

import com.ivancic.explorengv2.adapters.GalleryAdapter
import com.ivancic.explorengv2.databinding.ActivityGalleryBinding
import com.ivancic.explorengv2.models.Marked


class Gallery : AppCompatActivity() {
    lateinit var binding:ActivityGalleryBinding
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("markeri")

    companion object {
        var marker = ArrayList<Marked>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.returnToMenu.setOnClickListener {
            val intent = Intent(this,Menu::class.java)
            startActivity(intent)
        }

        database.addValueEventListener(object : ValueEventListener {
            var displayList = ArrayList<Marked>()

            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val a : List<Marked> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                        Marked::class.java)!! }

                    displayList.addAll(a)

                }catch (e : Exception){}
                for (g in marker) marker.remove(g)
                for (v in displayList)
                    if(Menu.currUser.collectedLocations.contains(v.id)) marker.add(v)

                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = GalleryAdapter(marker, this@Gallery,Menu.currUser) { position ->
                        val intent = Intent(this@Gallery, DetailsActivity::class.java).apply {
                            putExtra("pos", position)
                        }
                        startActivity(intent)
                    }
                }

                binding.lastCollected.text= buildString {
                append("Last collected:\n")
                append(Menu.currUser.lastCollectedLocation)
                }
                }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}