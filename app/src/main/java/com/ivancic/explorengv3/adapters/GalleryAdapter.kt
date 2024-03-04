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
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.Marked
import com.ivancic.explorengv3.models.User

class GalleryAdapter (
    private val markerList: ArrayList<Marked>,
    private val th : Context,
    private val currUser: User,
    private val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = GalleryItemBinding.inflate(LayoutInflater.from(th), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(markerList[position],th,currUser)
        holder.itemView.setOnClickListener { listener(position) }
    }

    override fun getItemCount(): Int {
        return markerList.size
    }

    class ViewHolder(private var itemBinding: GalleryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(marker: Marked, th: Context, currUser: User) {
            val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)!!
            itemBinding.name.text=marker.title

            itemBinding.description.text = if(locale.language=="hr") "Nadmorska visina: ${marker.alt}" else "Altitude: ${marker.alt}"
            itemBinding.dateColected.text = currUser.collectedLocationDates[marker.id]


            val sRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${marker.upimage}/${marker.image}.jpg")
            GlideApp.with(th).load(sRef).into(itemBinding.slidePic)

        }
    }


}