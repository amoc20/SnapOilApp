package com.example.snapoil.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.databinding.ActivitySelectStationBinding
import com.example.snapoil.model.Brand
import com.example.snapoil.model.GasStation
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class SelectStationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectStationBinding
    private lateinit var infoAdapter: InfoAdapter
    private lateinit var selectedBrand: Brand
    private var stationList: List<GasStation> = listOf()
    private val url = "http://localhost:5053/stations/"

    private val serverErrorMsg = "Server je nedostupný"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedBrand = Gson().fromJson(
            intent.getStringExtra(SelectBrandActivity.SELECTED_BRAND),
            Brand::class.java
        )

        loadStations()

        infoAdapter = InfoAdapter(stationList)

        binding.stationRecycler.layoutManager = LinearLayoutManager(this)
        binding.stationRecycler.adapter = infoAdapter

        infoAdapter.setOnInfoClickListener(object :
            InfoAdapter.OnInfoClickListener {
            override fun onInfoClick(position: Int, info: Any) {
                val intent = Intent(this@SelectStationActivity, UploadPhotoActivity::class.java)
                intent.putExtra(SELECTED_STATION, Gson().toJson(info))
                startActivity(intent)
            }
        })

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, SelectBrandActivity::class.java))
        }
    }

    private fun loadStations() {
        val request = JsonArrayRequest(
            Request.Method.GET, url + selectedBrand.id, null,
            { response ->
                stationList = Gson().fromJson(response.toString(), Array<GasStation>::class.java).toList()
                infoAdapter.setList(stationList)
                infoAdapter.notifyDataSetChanged()
            },
            { error ->
                Snackbar.make(binding.mainLayout, serverErrorMsg, Snackbar.LENGTH_LONG)
                    .setAction("ZKUSIT ZNOVU") {
                        loadStations()
                    }.show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    companion object {
        const val SELECTED_STATION = "selectedStation"
    }
}