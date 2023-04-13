package com.example.snapoil.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.databinding.ActivitySelectBrandBinding
import com.example.snapoil.model.Brand
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class SelectBrandActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectBrandBinding
    private lateinit var infoAdapter: InfoAdapter
    private val url = "http://localhost:5053/brands"

    private val serverErrorMsg = "Server je nedostupný"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadBrands()

        infoAdapter = InfoAdapter(listOf())

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
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val brandList = Gson().fromJson(response.toString(), Array<Brand>::class.java).toList()
                infoAdapter.setList(brandList)
                infoAdapter.notifyDataSetChanged()
            },
            { error ->
                Snackbar.make(binding.mainLayout, serverErrorMsg, Snackbar.LENGTH_LONG)
                    .setAction("ZKUSIT ZNOVU") {
                        loadBrands()
                    }.show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    companion object {
        const val SELECTED_BRAND = "selectedBrand"
    }
}