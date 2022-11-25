package com.ivancic.explorengv2.models


import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.ConfigurationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv2.activities.Menu
import com.ivancic.explorengv2.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MarkerWindow( private val mV: MapView, private val contr: Context, private val marked: Marked) :


    InfoWindow(R.layout.info_window, mV) {
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
    private val database2: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")


    private var dist=0.0
    private lateinit var locale: Locale
    override fun onOpen(item: Any?) {

        closeAllInfoWindowsOn(mV)

        val title = mView.findViewById<TextView>(R.id.text_view)
        val description = mView.findViewById<TextView>(R.id.description)
        val moveButton = mView.findViewById<Button>(R.id.move_button)
        val deleteButton = mView.findViewById<Button>(R.id.delete_button)
        val image = mView.findViewById<ImageView>(R.id.image)
        val large =mView.findViewById<ImageView>(R.id.large)
        val right = mView.findViewById<ImageView>(R.id.forward)
        val left = mView.findViewById<ImageView>(R.id.back)

        val locationOverlay1 = MyLocationNewOverlay(GpsMyLocationProvider(contr), mV)
        locationOverlay1.enableFollowLocation()
        locationOverlay1.enableMyLocation()

        locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)!!
        title.text=marked.title

        var b=marked.id
        var a=contr.getString(R.string.Distance)

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val latdist = sqrt((marked.lat!!.toDouble()- com.ivancic.explorengv2.activities.MainActivity.lon)*(marked.lat!!.toDouble()- com.ivancic.explorengv2.activities.MainActivity.lon))*111139
            val londist = sqrt((marked.lon!!.toDouble()- com.ivancic.explorengv2.activities.MainActivity.lat)*(marked.lon!!.toDouble()- com.ivancic.explorengv2.activities.MainActivity.lat))*111139
            dist=latdist+londist
            dist=(dist * 100.0).roundToInt() / 100.0
            a+="$dist m"
        }


        a += if(locale.language=="hr") "\n\n ${marked.desc}"
        else "\n\n ${marked.descEng}"
        description.text =a
        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${marked.upimage}/${marked.image}.jpg")
        GlideApp.with(contr).load(gsReference).into(image)
        GlideApp.with(contr).load(gsReference).into(large)



        var gsReference2: StorageReference


        var i=0
        if(i==0) left.setImageDrawable(getDrawable(contr, R.drawable.ic_baseline_arrow_back_24_g))
        right.setOnClickListener {
            if(i==marked.noImages!!.toInt())right.setImageDrawable(getDrawable(contr,
                R.drawable.ic_baseline_arrow_forward_24_g
            ))
            else {
                left.setImageDrawable(getDrawable(contr, R.drawable.ic_baseline_arrow_back_24))
                i++
                gsReference2 = storage.getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${marked.upimage}/${marked.image}$i.jpg")
                GlideApp.with(contr).load(gsReference2).into(large)
                if(i==marked.noImages!!.toInt())right.setImageDrawable(getDrawable(contr,
                    R.drawable.ic_baseline_arrow_forward_24_g
                ))
            }
        }
        left.setOnClickListener {
            if(i==1)left.setImageDrawable(getDrawable(contr, R.drawable.ic_baseline_arrow_back_24_g))
            else {
                right.setImageDrawable(getDrawable(contr, R.drawable.ic_baseline_arrow_forward_24))
                i--
                gsReference2 = storage.getReferenceFromUrl("gs://explore-ng.appspot.com/Markeri/${marked.upimage}/${marked.image}$i.jpg")
                if(i==1)left.setImageDrawable(getDrawable(contr, R.drawable.ic_baseline_arrow_back_24_g))
                GlideApp.with(contr).load(gsReference2).into(large)
            }
        }

        deleteButton.setOnClickListener{
            closeAllInfoWindowsOn(mV)
        }

        moveButton.setOnClickListener{
            if(dist<30){
                var isIt=!Menu.currUser.collectedLocations.contains(b)
                    if(!Menu.currUser.collectedLocations.contains(b)) collect()
                    else Toast.makeText(contr,contr.getString(R.string.collected),Toast.LENGTH_SHORT).show()
            }else Toast.makeText(contr,contr.getString(R.string.closer),Toast.LENGTH_SHORT).show()
        }

    }

    private fun collect() {
        Menu.triggerchange()
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val current = formatter.format(time)

        val newLocationList = Menu.currUser.collectedLocations
        val dateOfCollectedList = Menu.currUser.collectedLocationDates
        newLocationList.add(marked.id)
        dateOfCollectedList.add(current)
        val collectedLocationNumber=Menu.currUser.collectedLocationNumber+1
        val totalPoints=Menu.currUser.totalPoints+10
        database.child(Menu.currUser.uid).child("1").child("collectedLocations").setValue(newLocationList)
        database.child(Menu.currUser.uid).child("1").child("collectedLocationNumber").setValue(collectedLocationNumber)
        database.child(Menu.currUser.uid).child("1").child("totalPoints").setValue(totalPoints)
        database.child(Menu.currUser.uid).child("1").child("lastCollectedLocation").setValue(marked.title)
        database.child(Menu.currUser.uid).child("1").child("collectedLocationDates").setValue(dateOfCollectedList)
        database2.child(Menu.currUser.userNo.toString()).child("collectedLocationNumber").setValue(collectedLocationNumber)
        database2.child(Menu.currUser.userNo.toString()).child("totalPoints").setValue(totalPoints)
        congratulations(marked.title)
    }

    private fun congratulations(title: String?) {
        val alertDialogBuilder = AlertDialog.Builder(contr)
        alertDialogBuilder.setTitle(buildString {
            append(contr.getString(R.string.lokacija))
            append(": $title")
            append(contr.getString(R.string.colect)) })
        alertDialogBuilder.setMessage(buildString {
            append(contr.getString(R.string.congrats))
            append(contr.getString(R.string.lokacija))
            append(": $title")
            append(contr.getString(R.string.colect)) })

        alertDialogBuilder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(contr,
                contr.getString(R.string.no), Toast.LENGTH_SHORT).show()
        }
        alertDialogBuilder.show()
    }

    override fun onClose() {
        closeAllInfoWindowsOn(mV)
    }





}