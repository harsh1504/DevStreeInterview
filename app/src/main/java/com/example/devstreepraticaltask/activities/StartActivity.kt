package com.example.devstreepraticaltask.activities

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devstreepraticaltask.R
import com.example.devstreepraticaltask.adapter.MapDataAdapter
import com.example.devstreepraticaltask.database.MapDatabase
import com.example.devstreepraticaltask.databinding.ActivityStartBinding
import com.example.devstreepraticaltask.databinding.DeletePlaceDialogBinding
import com.example.devstreepraticaltask.databinding.SortDialogBinding
import com.example.devstreepraticaltask.model.MapModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StartActivity : AppCompatActivity(), MapDataAdapter.AdapterClick {

    lateinit var startBinding: ActivityStartBinding
    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            FetchSaveData()
        }
    }

    val updateResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            FetchSaveData()
        }
    }


    lateinit var context: Context
    lateinit var mapDb: MapDatabase
    lateinit var mapAdapter: MapDataAdapter
    lateinit var allDataList: ArrayList<MapModel>
    lateinit var progressDialog: ProgressDialog
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var deleteDialog: Dialog
    var deleteId: Int = 0
    var pos: Int = 0
    lateinit var deleteDialogBinding: DeletePlaceDialogBinding
    lateinit var sortDialog: BottomSheetDialog
    lateinit var sortDialogBinding: SortDialogBinding
    lateinit var sortType: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_start)

        startBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)

        initialization()
        FetchSaveData()
        allClickEvents()

    }

    private fun initialization() {
        context = this
        allDataList = ArrayList()
        mapDb = MapDatabase(context)
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        sortType = sharedPreferences.getString("Sort_order", "DESC").toString()

        deleteDialog = Dialog(context)
        deleteDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        deleteDialogBinding = DeletePlaceDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(deleteDialogBinding.root)

        sortDialog = BottomSheetDialog(context)
        sortDialogBinding = SortDialogBinding.inflate(layoutInflater)
        sortDialog.setContentView(sortDialogBinding.root)



    }

    private fun allClickEvents() {
        startBinding.btnStart.setOnClickListener {
            startForResult.launch(Intent(this@StartActivity, MapActivity::class.java))
        }

        startBinding.imgDirection.setOnClickListener {
            startActivity(Intent(this@StartActivity, ShowRootActivity::class.java))
        }

        startBinding.imgAddPoll.setOnClickListener {
//            startActivity(Intent(this@StartActivity, MapActivity::class.java))
            startForResult.launch(Intent(this@StartActivity, MapActivity::class.java))
        }

        deleteDialogBinding.imgCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialogBinding.imgOk.setOnClickListener {
            mapDb.deletePlace(deleteId)
//            mapAdapter.notifyDataSetChanged()
            mapAdapter.notify(deleteId, pos)
            deleteDialog.dismiss()
        }

        startBinding.imgSort.setOnClickListener {
            if (sharedPreferences.getString("Sort_order", "ASC").equals("ASC")) {
                sortDialogBinding.imgAsce.setImageResource(R.drawable.ic_checked)
                sortDialogBinding.imgDesc.setImageResource(R.drawable.ic_unchecked)
            } else {
                sortDialogBinding.imgAsce.setImageResource(R.drawable.ic_unchecked)
                sortDialogBinding.imgDesc.setImageResource(R.drawable.ic_checked)
            }
            sortDialog.show()
        }

        sortDialogBinding.imgClose.setOnClickListener {
            sortDialog.dismiss()
        }

        sortDialogBinding.imgClear.setOnClickListener {
            sortDialog.dismiss()
        }

        sortDialogBinding.constraintAsce.setOnClickListener {
            sortDialogBinding.imgAsce.setImageResource(R.drawable.ic_checked)
            sortDialogBinding.imgDesc.setImageResource(R.drawable.ic_unchecked)
            sortType = "ASC"
        }

        sortDialogBinding.constraintDesc.setOnClickListener {
            sortDialogBinding.imgAsce.setImageResource(R.drawable.ic_unchecked)
            sortDialogBinding.imgDesc.setImageResource(R.drawable.ic_checked)
            sortType = "DESC"
        }

        sortDialogBinding.imgApply.setOnClickListener {
            editor.putString("Sort_order", sortType)
            editor.commit()
            FetchSaveData()
            sortDialog.dismiss()
        }


    }

    private fun FetchSaveData() {
        val service: ExecutorService = Executors.newSingleThreadExecutor()
        service.execute {
            //PreExecution
            runOnUiThread {
                progressDialog = ProgressDialog(context)
                progressDialog.setCancelable(false)
                progressDialog.setMessage("Please Wait...")
                progressDialog.show()
            }

            //DoInBackground
            allDataList = mapDb.getModelData1(sortType)


            //PostExecution
            runOnUiThread {

                if (allDataList.size > 0) {
                    startBinding.constraintData.visibility = View.VISIBLE
                    startBinding.constraintNoData.visibility = View.GONE
                    startBinding.rvMapData.layoutManager = LinearLayoutManager(this)
                    mapAdapter = MapDataAdapter(context, allDataList, this@StartActivity)
                    startBinding.rvMapData.adapter = mapAdapter
                    progressDialog.dismiss()
                } else {
                    startBinding.constraintData.visibility = View.GONE
                    startBinding.constraintNoData.visibility = View.VISIBLE
                }
                progressDialog.dismiss()
            }
        }
    }

    override fun delete(id: Int, position: Int) {
//        mapDb.deletePlace(id)
//        mapAdapter.notifyDataSetChanged()
        deleteId = id
        pos = position
        deleteDialog.show()

    }

    override fun updateData(id: Int, pName: String, latlng: String, uLat: Double, uLongt: Double) {

        updateResult.launch(Intent(context, MapActivity::class.java)
            .putExtra("id", id)
            .putExtra("placeName", pName)
            .putExtra("latlong", latlng)
            .putExtra("latitude", uLat)
            .putExtra("longitude", uLongt)
            .putExtra("isUpdate", true))
    }


}