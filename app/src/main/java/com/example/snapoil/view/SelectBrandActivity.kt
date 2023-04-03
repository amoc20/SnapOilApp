package com.example.snapoil.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.databinding.ActivitySelectBrandBinding
import com.example.snapoil.model.Brand
import com.google.gson.Gson

class SelectBrandActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectBrandBinding
    private var brandList: List<Brand> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadBrands()
        //brandList = listOf(Brand(1, "Ojl a.s."))

        val infoAdapter = InfoAdapter(brandList)

        binding.brandRecycler.layoutManager = LinearLayoutManager(this)
        binding.brandRecycler.adapter = infoAdapter

        infoAdapter.setOnInfoClickListener(object :
            InfoAdapter.OnInfoClickListener {
            override fun onInfoClick(position: Int, info: Any) {
                val intent = Intent(this@SelectBrandActivity, SelectStationActivity::class.java)
                intent.putExtra(SELECTED_BRAND, Gson().toJson(info))
                startActivity(intent)
            }
        })

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loadBrands() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.113:5053/brands"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                brandList = Gson().fromJson(response.toString(), Array<Brand>::class.java).toList()
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

    companion object {
        const val SELECTED_BRAND = "selectedBrand"
    }
}