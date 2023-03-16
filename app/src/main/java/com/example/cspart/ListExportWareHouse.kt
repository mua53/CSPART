package com.example.cspart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMaterial
import com.example.cspart.models.ExportWareHouseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListExportWareHouse : AppCompatActivity() {
    private var requestCode: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_export_ware_house)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"

//        var listMaterial = intent.getSerializableExtra("listMaterial") as ArrayList<Material>
        requestCode = intent.getStringExtra("requestCode").toString()
        bindingData(this)
//        for (item in listMaterial) {
//            item.requestCode = requestCode
//        }
//        listView.adapter = AdapterMaterial(this,R.layout.material_detail, listMaterial)
//        val list = listOf(requestCode)
//        listView.setOnItemClickListener { adapterView:AdapterView<*>, view:View, i:Int, l:Long ->
//            val itemAtPos  = adapterView.getItemAtPosition(i)
//            var itemMaterial = listMaterial[i]
//            val intent = Intent(applicationContext, ScreenMaterialDeail::class.java)
//            intent.putExtra("requestCode",itemMaterial.requestCode)
//            intent.putExtra("materialCode",itemMaterial.materialCode)
//            startActivity(intent)
//            Toast.makeText(this@ListExportWareHouse, "Test" + i.toString(), Toast.LENGTH_LONG).show()
//        }
    }

    override fun onResume() {
        super.onResume()
        bindingData(this)
    }

    private fun  bindingData(listExportWareHouse: ListExportWareHouse) {
        var listView = findViewById<ListView>(R.id.lstMaterialDetail)
        RetrofitClient.instance.getDeliveryRequestForm(requestCode)
            .enqueue(object: Callback<ExportWareHouseResponse> {
                override fun onFailure(call: Call<ExportWareHouseResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ExportWareHouseResponse>, response: Response<ExportWareHouseResponse>) {
                    if(response.body()!!.statusCode == 1){
                        val listMaterial = response.body()!!.listMaterial
                        var arrayList = ArrayList(listMaterial)
                        for (item in listMaterial) {
                            item.requestCode = requestCode
                        }
                        listView.adapter = AdapterMaterial(listExportWareHouse,R.layout.material_detail, arrayList)
                        val list = listOf(requestCode)
                        listView.setOnItemClickListener { adapterView:AdapterView<*>, view:View, i:Int, l:Long ->
                            val itemAtPos  = adapterView.getItemAtPosition(i)
                            var itemMaterial = listMaterial[i]
                            val intent = Intent(applicationContext, MaterialDetail::class.java)
                            intent.putExtra("requestCode",itemMaterial.requestCode)
                            intent.putExtra("materialCode",itemMaterial.materialCode)
                            intent.putExtra("item",itemMaterial)
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                    }

                }
            })
    }
}