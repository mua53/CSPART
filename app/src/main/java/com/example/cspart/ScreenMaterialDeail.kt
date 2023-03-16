package com.example.cspart

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.MaterialDetailResponse
import com.example.cspart.models.MaterialImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenMaterialDeail : AppCompatActivity() {
    var arrayListImage: List<MaterialImage>? = null
    var indexImage: Int = 0
    var lengthImage: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_material_deail)
        var requestCode = intent.getStringExtra("requestCode").toString()
        var materialCode = intent.getStringExtra("materialCode").toString()
        RetrofitClient.instance.getDetailDeliveryRequestForm(requestCode,materialCode)
            .enqueue(object: Callback<MaterialDetailResponse> {
                override fun onFailure(call: Call<MaterialDetailResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<MaterialDetailResponse>, response: Response<MaterialDetailResponse>) {
                    if (response.body()?.statusCode  == 1) {
                        var txtNameMaterial = findViewById<TextView>(R.id.txtNameMaterial)
                        txtNameMaterial.text = response.body()!!.data.materialName
                        var txtCode = findViewById<TextView>(R.id.edtMaterialCode)
                        txtCode.text = response.body()!!.data.materialCode
                        var editTextMaterialName = findViewById<EditText>(R.id.edtMaterialName)
                        editTextMaterialName.setText(response.body()!!.data.materialName)
                        var editTextMaterialCode = findViewById<EditText>(R.id.edtMaterialCode)
                        editTextMaterialCode.setText(response.body()!!.data.materialCode)
                        var txtNumber = findViewById<TextView>(R.id.txtNumber)
                        txtNumber.text = "Số lượng: (" + response.body()!!.data.quantityRequest.toString() + ")"
                        arrayListImage = response.body()!!.data.materialImage
                        lengthImage = arrayListImage!!.size
                    }
                }
            })
        initAction()
    }

    private fun initAction() {
//        var btnNext = findViewById<Button>(R.id.btnNextImg)
//        btnNext.setOnClickListener { loadImage(0) }
//        var btnBack = findViewById<Button>(R.id.btnBack)
//        btnBack.setOnClickListener { loadImage(1) }
        var btnPrint = findViewById<Button>(R.id.btnSaveReport)
        btnPrint.setOnClickListener { printQRCode() }
    }

    private fun printQRCode() {
        var txtCode = findViewById<TextView>(R.id.edtMaterialCode).text.toString()
//        var barcodeEncoder = BarcodeEncoder()
//        var bitmap = barcodeEncoder.encodeBitmap(txtCode, BarcodeFormat.QR_CODE, 512, 512)
//        var imageQR = findViewById<ImageView>(R.id.idIVQrcode)
//        imageQR.setImageBitmap(bitmap)
    }

    private fun loadImage(typeOfBtn:Int) {
//        if (typeOfBtn == 0) {
//            if (indexImage < lengthImage) {
//                indexImage++
//                var materialImage = arrayListImage?.get(indexImage)
//                var url = materialImage?.imageLink
//                if (url != null) {
//                    var photoView = findViewById<PhotoView>(R.id.ImgMaterial)
//                    Picasso.get()
//                        .load(url)
//                        .into(photoView)
//                }
//            }
//        } else {
//            if (indexImage > 0) {
//                indexImage = indexImage - 1
//                var materialImage = arrayListImage?.get(indexImage)
//                var url = materialImage?.imageLink
//                if (url != null) {
//                    var photoView = findViewById<PhotoView>(R.id.ImgMaterial)
//                    Picasso.get()
//                        .load(url)
//                        .into(photoView)
//                }
//            }
//        }
    }
}