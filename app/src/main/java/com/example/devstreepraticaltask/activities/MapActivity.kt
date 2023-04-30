package com.example.devstreepraticaltask.activities

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.devstreepraticaltask.database.MapDatabase
import com.example.devstreepraticaltask.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapBinding: ActivityMapBinding
    lateinit var context: Context
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var placeName: String
    lateinit var saveLatLong: String
    lateinit var latLng: LatLng
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var mapDb: MapDatabase
    lateinit var coder: Geocoder
    lateinit var address: List<Address>
//    var p1: GeoPoint? = null
    lateinit var latLngList: ArrayList<LatLng>
    lateinit var latLngData: String
    var updateId:Int = 0
    lateinit var updatePlaceName: String
    lateinit var updateLatlong: String
    var updateLatitude: Double = 38.685516
    var updateLongitude: Double = -101.073324
    var isUpdate: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map)

        mapBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)

        initialization()
        allClickEvent()

    }

    private fun allClickEvent() {
        mapBinding.imgBackButton.setOnClickListener {
            onBackPressed()
        }

        mapBinding.mapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location: String = mapBinding.mapSearch.query.toString()
                var addressList: List<Address>? = null
                val geopoint: GeoPoint? = null


                if (location != null || location == "") {
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address: Address = addressList!![0]
                    latLng = LatLng(address.latitude, address.longitude)
                    val gson = Gson()
                    saveLatLong = gson.toJson(latLng)

                    latitude = address.latitude
                    longitude = address.longitude

                    latLngList.add(latLng)

                    googleMap.addMarker(MarkerOptions().position(latLng).title(location))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    mapBinding.constraintSave.visibility = View.VISIBLE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        mapFragment.getMapAsync(this);

        mapBinding.btnSave.setOnClickListener {
            placeName = mapBinding.mapSearch.query.toString()
            if (placeName.isEmpty()) {
                Toast.makeText(context, "Proper Search First", Toast.LENGTH_SHORT).show()
            } else {
                if (isUpdate) {
                    mapBinding.constraintSave.visibility = View.GONE
                    mapDb.updateData(updateId,placeName, saveLatLong, latitude, longitude)
                    Toast.makeText(context, "Place Update Successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()

                } else {
                    mapBinding.constraintSave.visibility = View.GONE
                    mapDb.insertLocationData(placeName, saveLatLong, latitude, longitude)
                    Toast.makeText(context, "Place Save Successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

    }

    private fun initialization() {
        context = this
        mapFragment = (supportFragmentManager.findFragmentById(com.example.devstreepraticaltask.R.id.fragmentMap) as SupportMapFragment?)!!
        mapDb = MapDatabase(context)

        latLngList = ArrayList()

        updateId = intent.getIntExtra("id", 0)
        updateLatlong = intent.getStringExtra("latlong").toString()
        updateLatitude = intent.getDoubleExtra("latitude", 38.685516)
        updateLongitude = intent.getDoubleExtra("longitude", -101.073324)
        isUpdate = intent.getBooleanExtra("isUpdate", false)
    }

    override fun onMapReady(googleMap1: GoogleMap) {
        googleMap = googleMap1;
        if (isUpdate) {
            if (updateLatlong != null && updateLatitude != null && updateLongitude != null) {
                val latLng56 = LatLng(updateLatitude, updateLongitude)
                val cameraPosition = CameraPosition.builder()
                    .target(latLng56)
                    .zoom(10F)
                    .tilt(30F)
                    .build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }

    }

    override fun onBackPressed() {
        if (mapBinding.constraintSave.visibility == View.VISIBLE) {
            mapBinding.constraintSave.visibility = View.GONE
        }
        super.onBackPressed()
    }

}