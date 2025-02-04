package com.ivancic.explorengv3.activities

import android.Manifest.permission.*
import android.annotation.SuppressLint

import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityMainBinding
import com.ivancic.explorengv3.models.Marked
import com.ivancic.explorengv3.models.MarkerWindow
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber


@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : AppCompatActivity()  {
    private val FINE_LOCATION_RQ= 101
    private val WRITE_EXTERNAL_RQ=102

    private val REQUEST_PERMISSIONS_REQUEST_CODE=1

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient //https://developer.android.com/training/location/retrieve-current
    private var lastLoction: Location? = null
    private var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates = false

    companion object {
        const val REQUEST_CHECK_SETTINGS = 20202
        var lat:Double =0.0
        var lon:Double =0.0
    }

    private lateinit var map : MapView
    lateinit var binding: ActivityMainBinding
    private lateinit var mapController : IMapController
    private var userId:String=""
    private val database: DatabaseReference =
        FirebaseDatabase.
        getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").
        getReference("markeri")

    init {

        locationRequest = LocationRequest.Builder(1000)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {
                    // Update UI with location data
                    updateLocation(location) //MY function
                }
            }
        }

        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            Timber.d("Permissions granted $allAreGranted")
            if (allAreGranted) {
                initCheckLocationSettings()
                //initMap() if settings are ok
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) //Init report type
        }

        getInstance()
            .load(applicationContext, this.getPreferences(Context.MODE_PRIVATE))
        val appPerms = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_NETWORK_STATE,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            INTERNET
        )
        activityResultLauncher.launch(appPerms)


        checkForPermissions(WRITE_EXTERNAL_STORAGE,getString(R.string.dozvola_pisanje_vanjska_memorija),WRITE_EXTERNAL_RQ)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)





        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                // Got last known location. In some rare situations this can be null.
            }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId= intent.getStringExtra("uid").toString()


        database.addValueEventListener(object : ValueEventListener {
            var displayList = ArrayList<Marked>()
            var marker = ArrayList<Marker>()
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val a : List<Marked> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                        Marked::class.java)!! }

                    displayList.addAll(a)

                }catch (e : Exception){
                }

                marker.add(Marker(binding.map))

                for ((i, v) in displayList.withIndex()) {
                    marker[i].position = GeoPoint(v.lon!!.toDouble() , v.lat!!.toDouble())
                    marker[i].icon = ContextCompat.getDrawable(this@MainActivity, setImgDrawable(v.type))
                    marker[i].setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                    val infoWindow = MarkerWindow(binding.map, this@MainActivity, v)
                    marker[i].infoWindow = infoWindow
                    binding.map.overlays.add(marker[i])
                    binding.map.invalidate()
                    marker.add(Marker(binding.map))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setZoomRounding(true)
        map.setMultiTouchControls(true)
        mapController = map.controller
        mapController.setZoom(16.0)

        val startPoint = GeoPoint(45.2632756, 17.37428)
        mapController.setCenter(startPoint)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val minimapOverlay = MinimapOverlay(this, map.tileRequestCompleteHandler)
        minimapOverlay.width = width / 5
        minimapOverlay.height = height / 5

        map.overlays.add(minimapOverlay)


        val scaleBarOverlay = ScaleBarOverlay(map)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(width / 2, 10)
        map.overlays.add(scaleBarOverlay)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

    }


    private fun setImgDrawable(type: String?): Int {

        when (type){
            "auto"          -> return R.drawable.auto_icon
            "barracks"      -> return R.drawable.barracks_icon
            "brewary"       -> return R.drawable.brewary_icon
            "building"      -> return R.drawable.building_icon
            "cemetery"      -> return R.drawable.cemetery_icon
            "church"        -> return R.drawable.church_icon
            "court"         -> return R.drawable.court_icon
            "deptstore"     -> return R.drawable.deptstore_icon
            "field"         -> return R.drawable.field_icon
            "forest"        -> return R.drawable.forest_icon
            "fountain"      -> return R.drawable.fountain_icon
            "hall"          -> return R.drawable.hall_icon
            "hospital"      -> return R.drawable.hospital_icon
            "hotel"         -> return R.drawable.hotel_icon
            "jewish"        -> return R.drawable.jewish_icon
            "lake"          -> return R.drawable.lake_icon
            "library"       -> return R.drawable.library_icon
            "market"        -> return R.drawable.market_icon
            "memorial"      -> return R.drawable.memorial_icon
            "park"          -> return R.drawable.park_icon
            "pool"          -> return R.drawable.pool_icon
            "powerplant"    -> return R.drawable.powerplant_icon
            "printing"      -> return R.drawable.printing_icon
            "school"        -> return R.drawable.school_icon
            "settlement"    -> return R.drawable.settlement_icon
            "src"           -> return R.drawable.src_icon
            "stadium"       -> return R.drawable.stadium_icon
            "tower"         -> return R.drawable.tower_icon
            "transceiver"   -> return R.drawable.transceiver_icon
            "venera"        -> return R.drawable.venera_icon
            "trail"         -> return R.drawable.walkingtrail_icon
        }
        return 0
    }


    private fun checkForPermissions(permission: String, name: String, requestCode: Int){

            when{
                ContextCompat.checkSelfPermission(applicationContext,permission)== PackageManager.PERMISSION_GRANTED ->{
                    Toast.makeText(applicationContext,name + getString(R.string.odobreno),Toast.LENGTH_SHORT).show()

                }
                shouldShowRequestPermissionRationale(permission)-> showDialog(permission,name,requestCode)

                else-> ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)

            }


    }
   private fun showDialog (permission: String, name: String, requestCode: Int){
        val builder =AlertDialog.Builder(this)

        builder.apply{
            setMessage (buildString {
        append(getString(R.string.dopustenje))
        append(name)
        append(getString(R.string.koristenje))
    })
            setTitle(getString(R.string.dopustenje2))
            setPositiveButton("OK"){ _, _ ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
            }
        }
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()

        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)
        val compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
        map.invalidate()
    }


    override fun onPause() {

        super.onPause()

        if (requestingLocationUpdates) {
            requestingLocationUpdates = false
            stopLocationUpdates()
        }

        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)
        val compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)
        map.invalidate()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("Settings onActivityResult for $requestCode result $resultCode")
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                initMap()
            }
        }
    }

    private fun initLoaction() { //call in create
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        readLastKnownLocation()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() { //onResume
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    private fun stopLocationUpdates() { //onPause
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission") //permission are checked before
    fun readLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { updateLocation(it) }
            }
    }
    private fun initCheckLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Timber.d("Settings Location IS OK")
            //MyEventLocationSettingsChange.globalState = true //default
            initMap()

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {

                Timber.d("Settings Location addOnFailureListener call settings")
                try {

                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Timber.d("Settings Location sendEx??")
                }
            }
        }

    }

    fun updateLocation(newLocation: Location) {
        lastLoction = newLocation
        lat =newLocation.latitude
        lon =newLocation.longitude
        map.invalidate()

    }

    private fun initMap() {
        initLoaction()
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        map.invalidate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String){
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, buildString {
        append(name)
        append(getString(R.string.odbijena_dozvola))
    },Toast.LENGTH_SHORT).show()
                when (requestCode){
                    WRITE_EXTERNAL_RQ -> {
                        checkForPermissions(
                            ACCESS_COARSE_LOCATION,
                            getString(R.string.gruba_lokacija),
                            REQUEST_PERMISSIONS_REQUEST_CODE
                        )

                    }
                    REQUEST_PERMISSIONS_REQUEST_CODE -> checkForPermissions(ACCESS_FINE_LOCATION,getString(
                        R.string.lokacija
                    ),FINE_LOCATION_RQ)
                }
            }else{
                Toast.makeText(applicationContext, buildString {
        append(name)
        append(getString(R.string.dozvola_prihvacena))
    },Toast.LENGTH_SHORT).show()
                when (requestCode){
                    FINE_LOCATION_RQ-> checkForPermissions(ACCESS_COARSE_LOCATION,getString(R.string.gruba_lokacija),REQUEST_PERMISSIONS_REQUEST_CODE)
                    WRITE_EXTERNAL_RQ -> checkForPermissions(ACCESS_FINE_LOCATION,getString(R.string.lokacija),FINE_LOCATION_RQ)
                }
            }
        }
        when(requestCode){
            FINE_LOCATION_RQ ->innerCheck(getString(R.string.lokacija))
            REQUEST_PERMISSIONS_REQUEST_CODE ->innerCheck(getString(R.string.gruba_lokacija))
            WRITE_EXTERNAL_RQ -> innerCheck(getString(R.string.dozvola_pisanje_vanjska_memorija))
        }
    }

}