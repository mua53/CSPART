package com.example.cspart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMetarialInput
import com.example.cspart.models.Input
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenListInput : AppCompatActivity() {
    private var requestCode: String = ""
    private var currentPoint: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_input)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        bindingData()
    }

    private fun bindingData() {
        requestCode = intent.getStringExtra("requestCode").toString()
        var listView = findViewById<ListView>(R.id.lstMaterialInputDetail)

        RetrofitClient.instance.getIntput(requestCode)
            .enqueue(object: Callback<Input> {
                override fun onFailure(call: Call<Input>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<Input>, response: Response<Input>) {
                    if(response.body()!!.status == 1){
                        val listMaterial = response.body()!!.material
                        var arrayList = ArrayList(listMaterial)
                        var lstArea = response.body()!!.area
                        var lstSerialDetail = response.body()!!.serialDetail
                        for (item in listMaterial!!) {
                            item.inputCode = requestCode
                            item.lstArea = lstArea
                            item.lstSerialDetail= lstSerialDetail
                        }

                        listView.adapter = AdapterMetarialInput(this@ScreenListInput,R.layout.material_input_detail, arrayList)
                        val list = listOf(requestCode)
                        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
                            val itemAtPos  = adapterView.getItemAtPosition(i)
                            currentPoint = i
                            var itemMaterial = listMaterial?.get(i)
                            val intent = Intent(applicationContext, ScreentMaterialInputDetail::class.java)

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