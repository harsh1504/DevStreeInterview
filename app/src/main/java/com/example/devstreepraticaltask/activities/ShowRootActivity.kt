package com.example.devstreepraticaltask.activities

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.devstreepraticaltask.R
import com.example.devstreepraticaltask.database.MapDatabase
import com.example.devstreepraticaltask.databinding.ActivityShowRootBinding
import com.example.devstreepraticaltask.model.MapModel
import com.example.devstreepraticaltask.utils.GoogleMapDTO
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class ShowRootActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {

    lateinit var showRootBinding: ActivityShowRootBinding
    lateinit var googleMap: GoogleMap
    lateinit var mapDb: MapDatabase
    lateinit var context: Context
    lateinit var supportMapFragment: SupportMapFragment
    lateinit var googleApiClient: GoogleApiClient
    lateinit var allData: ArrayList<MapModel>
    lateinit var latLonglist: ArrayList<String>
    lateinit var location1: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_show_root)

        showRootBinding = ActivityShowRootBinding.inflate(layoutInflater)
        setContentView(showRootBinding.root)

        initialization()

    }

    private fun initialization() {
        context = this
        mapDb = MapDatabase(context)

        latLonglist = ArrayList()

        supportMapFragment = (supportFragmentManager.findFragmentById(R.id.rootMap) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)
        supportMapFragment.getView()

        googleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(LocationServices.API)
            .build()

//        allData = mapDb.modelData
//        latLonglist = mapDb.allLatLong

    }

    inner class GetDirection(val url: String): AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body().toString()
            val result = ArrayList<List<LatLng>>()
            try {
                val responseObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0..(responseObj.routes[0].legs[0].steps.size -1)) {
                    /*val startLatLng = LatLng(responseObj.routes[0].legs[0].steps[i].start_location.lat.toDouble(),
                        responseObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
                    path.add(startLatLng)
                    val endLatLng = LatLng(responseObj.routes[0].legs[0].steps[i].end_location.lat.toDouble(),
                        responseObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.add(endLatLng)*/

                    path.addAll(decodePolyline(responseObj.routes[0].legs[0].steps[i].polyline.points))

                }
                Log.d("TAG", "doInBackground: ${path.size}" )
                result.add(path)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
//            super.onPostExecute(result)
            val lineOption = PolylineOptions()
            for (i in result.indices) {
                Log.d("TAG", "onPostExecute: " + "insixe")
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(Color.BLUE)
                lineOption.geodesic(true)
            }
            googleMap.addPolyline(lineOption)
            Log.d("TAG", "onPostExecute: ")
        }

    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving"
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        val location1 = LatLng(21.170240, 72.831062)
        googleMap.addMarker(MarkerOptions().position(location1))
        val cameraPosition = CameraPosition.Builder()
            .target(location1)
            .bearing(90f)
            .zoom(17f)
            .tilt(30f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val location2 = LatLng(23.033863, 72.585022)
        googleMap.addMarker(MarkerOptions().position(location2))
        val cameraPosition1 = CameraPosition.Builder()
            .target(location2)
            .zoom(17f)
            .tilt(30f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1))

        val URL = getDirectionURL(location1, location2)
        GetDirection(URL).execute()

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    private fun decodePolyline(encode: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encode.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encode[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encode[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

}