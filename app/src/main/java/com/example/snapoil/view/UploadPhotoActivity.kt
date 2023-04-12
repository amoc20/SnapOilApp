package com.example.snapoil.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.snapoil.api.FileDataPart
import com.example.snapoil.api.VolleyFileUploadRequest
import com.example.snapoil.databinding.ActivityUploadPhotoBinding
import com.example.snapoil.model.GasStation
import com.example.snapoil.model.GasStationPrices
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.Charset


class UploadPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadPhotoBinding
    private lateinit var selectedStation: GasStation
    private var imageData: ByteArray? = null

    private val uploadImageUrl = "http://localhost:5053/uploadimage"
    private val uploadPricesUrl = "http://localhost:5053/uploadprices"
    private val serverErrorMsg = "Server je nedostupný"
    private val blankErrorMsg = "Vyplňte hodnoty"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedStation = Gson().fromJson(
            intent.getStringExtra(SelectStationActivity.SELECTED_STATION),
            GasStation::class.java
        )

        binding.uploadImageView.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForResult.launch(gallery)
        }

        binding.confirmBtn.setOnClickListener {
            binding.confirmBtn.isEnabled = false
            binding.loadingPanel.visibility = View.VISIBLE
            postPrices()
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, SelectStationActivity::class.java)
            intent.putExtra(SelectBrandActivity.SELECTED_BRAND, Gson().toJson(selectedStation.brand))
            startActivity(intent)
        }
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            binding.uploadImageView.setImageURI(imageUri)
            createImageData(imageUri!!)
            postImage()
        }
    }

    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
        inputStream?.close()
    }

    private fun postImage() {
        val request = object : VolleyFileUploadRequest(
            Method.POST, uploadImageUrl,
            Response.Listener
            { response ->
                val jsonString = String(response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                val result = Gson().fromJson(jsonString, GasStationPrices::class.java)
                binding.natural95EditText.setText(result.natural95.toString())
                binding.dieselEditText.setText(result.diesel.toString())
            },
            Response.ErrorListener
            { error ->
                Toast.makeText(this, serverErrorMsg, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                params["file"] = FileDataPart(
                    selectedStation.brand.name, imageData!!, "jpeg")
                return params
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun postPrices() {
        val natural95Text = binding.natural95EditText.text
        val dieselText = binding.dieselEditText.text

        if (natural95Text.isNullOrBlank() || dieselText.isNullOrBlank()) {
            Toast.makeText(this, blankErrorMsg, Toast.LENGTH_LONG).show()
            binding.confirmBtn.isEnabled = true
            binding.loadingPanel.visibility = View.INVISIBLE
            return
        }

        val prices = GasStationPrices(
            selectedStation.id,
            natural95Text.toString().trim().toDouble(),
            dieselText.toString().trim().toDouble()
        )

        val request = JsonObjectRequest(
            Request.Method.POST, uploadPricesUrl, JSONObject(Gson().toJson(prices)),
            { response ->
                val result = Gson().fromJson(response.toString(), GasStationPrices::class.java)
                binding.loadingPanel.visibility = View.INVISIBLE
                if (result == prices) {
                    // TODO show success
                } else {
                    //TODO show error
                }
            },
            { error ->
                binding.confirmBtn.isEnabled = true
                binding.loadingPanel.visibility = View.INVISIBLE
                Toast.makeText(this, serverErrorMsg, Toast.LENGTH_LONG).show()
            }
        )

        request.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(request)
    }
}