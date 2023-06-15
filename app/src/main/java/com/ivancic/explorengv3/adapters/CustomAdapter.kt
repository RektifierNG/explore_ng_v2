package com.ivancic.explorengv3.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv3.databinding.UserItemBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.User4Leaderboard

class CustomAdapter(
    private val userList: ArrayList<User4Leaderboard>,
    private val th : Context
) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = UserItemBinding.inflate(LayoutInflater.from(th), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(userList[position],th)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(private var itemBinding: UserItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(user: User4Leaderboard, th: Context) {
            itemBinding.name.text=user.name
            itemBinding.noPointsQuiz.text=user.totalQuizPoint.toString()
            itemBinding.pointCollected.text=(user.collectedLocationNumber*10).toString()
            itemBinding.totalPoints.text=user.totalPoints.toString()

            if (user.pic==0){
                val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("users/profile.jpg")
                GlideApp.with(th).load(sRef).into(itemBinding.slidePic)
            }
            else {
                val sRef: StorageReference =
                    FirebaseStorage.getInstance().reference.child("users/ ${user.uid}/profile${user.pic}.jpg")
                GlideApp.with(th).load(sRef).into(itemBinding.slidePic)
            }

        }
    }
}