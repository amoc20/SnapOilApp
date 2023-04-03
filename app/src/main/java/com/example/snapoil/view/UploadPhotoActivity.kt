package com.example.snapoil.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.databinding.ActivityUploadPhotoBinding
import com.example.snapoil.model.GasStationPrices
import com.google.gson.Gson

class UploadPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadPhotoBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadPhotoBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForResult.launch(gallery)
            //postImage()
        }

        binding.confirmBtn.setOnClickListener {
            //postPrices()
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, SelectStationActivity::class.java))
        }
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.uploadImageView.setImageURI(imageUri)
        }
    }

    private fun postImage() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.113:5053/uploadimage"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            { response ->
                val prices = Gson().fromJson(response.toString(), GasStationPrices::class.java)
                binding.natural95EditText.setText(prices.natural95.toString())
                binding.dieselEditText.setText(prices.diesel.toString())
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun postPrices() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.113:5053/uploadprices"
    }
}