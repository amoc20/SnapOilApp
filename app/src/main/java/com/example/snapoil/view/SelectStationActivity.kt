package com.example.snapoil.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.databinding.ActivitySelectStationBinding
import com.example.snapoil.model.Brand
import com.example.snapoil.model.GasStation
import com.google.gson.Gson

class SelectStationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectStationBinding
    private var stationList: List<GasStation> = listOf()
    private lateinit var selectedBrand: Brand

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectStationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedBrand = Gson().fromJson(
            intent.getStringExtra(SelectBrandActivity.SELECTED_BRAND),
            Brand::class.java
        )

        loadStations()
        //stationList = listOf(GasStation(1, "Kolín", "Ovčárecká", selectedBrand))

        val infoAdapter = InfoAdapter(stationList)

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
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.113:5053/stations/${selectedBrand.id}"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                stationList = Gson().fromJson(response.toString(), Array<GasStation>::class.java).toList()
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

    companion object {
        const val SELECTED_STATION = "selectedStation"
    }
}