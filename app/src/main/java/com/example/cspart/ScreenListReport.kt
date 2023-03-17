package com.example.cspart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMaterialReport
import com.example.cspart.models.ReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenListReport : AppCompatActivity() {
    private var reportCode: String = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_report)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        bindingData()
    }

    private fun bindingData() {
        reportCode = intent.getStringExtra("requestCode").toString()
        var listView = findViewById<ListView>(R.id.lstMaterialReportDetail)

        RetrofitClient.instance.getInventoryWarehouse(reportCode)
            .enqueue(object: Callback<ReportResponse> {
                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                    if(response.body()!!.status == 1){
                        val listMaterial = response.body()!!.data?.material
                        var arrayList = ArrayList(listMaterial)
                        for (item in listMaterial!!) {
                            item.inputCode = reportCode
                        }

                        listView.adapter = AdapterMaterialReport(this@ScreenListReport,R.layout.material_input_detail, arrayList)
                        val list = listOf(reportCode)
                        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
                            val itemAtPos  = adapterView.getItemAtPosition(i)
                            var itemMaterial = listMaterial?.get(i)
                            val intent = Intent(applicationContext, ScreenReportDetail::class.java)

                            intent.putExtra("materialCode", itemMaterial?.materialCode)
                            intent.putExtra("inputCode",itemMaterial?.inputCode)
//                            intent.putExtra("areaCode",response.body()!!.data?.areaCode)
                            intent.putExtra("item",itemMaterial)
                            startActivity(intent)
                        }
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