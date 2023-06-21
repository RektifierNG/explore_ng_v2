package com.ivancic.explorengv3.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv3.databinding.GalleryItemBinding
import com.ivancic.explorengv3.databinding.MenuItemBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.Marked
import com.ivancic.explorengv3.models.User

class MenuAdapter (
    private val imageList: ArrayList<Int>,
    private val th : Context,
    private val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = MenuItemBinding.inflate(LayoutInflater.from(th), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pos =position % 8
        holder.bindItem(imageList[pos],th)
        holder.itemView.setOnClickListener { listener(pos) }
    }

    override fun getItemCount(): Int {
        return 17
    }

    class ViewHolder(private var itemBinding: MenuItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(image: Int, th: Context) {

            GlideApp.with(th).load(image).into(itemBinding.image)

        }
    }


}