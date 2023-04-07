package com.example.cspart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.R.id.lstMaterialExportDetail
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMaterialExport
import com.example.cspart.models.PackingListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenListExportWarehouse : AppCompatActivity() {
    private var exportWarehouseCode: String = ""
    private var currentPoint: Int = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_export_warehouse)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        bindingData()
    }

    private fun bindingData(){
        exportWarehouseCode = intent.getStringExtra("exportWarehouseCode").toString()
        var listView = findViewById<ListView>(lstMaterialExportDetail)
        RetrofitClient.instance.getPackList(exportWarehouseCode)
            .enqueue(object: Callback<PackingListResponse> {
                override fun onFailure(call: Call<PackingListResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<PackingListResponse>, response: Response<PackingListResponse>) {
                    if(response.body()!!.status == 1){
                        val listMaterial = response.body()!!.material
                        var arrayList = ArrayList(listMaterial)
                        for (item in listMaterial!!) {
                            item.inputCode = exportWarehouseCode
                        }

                        listView.adapter = AdapterMaterialExport(this@ScreenListExportWarehouse,R.layout.material_input_detail, arrayList)
                        val list = listOf(exportWarehouseCode)
                        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
                            val itemAtPos  = adapterView.getItemAtPosition(i)
                            currentPoint = i
                            var itemMaterial = listMaterial?.get(i)
                            val intent = Intent(applicationContext, ScreenMaterialExportDetail::class.java)

                            intent.putExtra("materialCode", itemMaterial?.materialCode)
                            intent.putExtra("inputCode",itemMaterial?.inputCode)
                            intent.putExtra("item",itemMaterial)
                            startActivity(intent)
                        }
                        listView.setSelection(currentPoint)
                    }else{
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })

    }

    override fun onResume() {
        super.onResume()
        bindingData()
    }

}