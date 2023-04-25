package com.example.cspart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMaterial
import com.example.cspart.models.AdapterMetarialInput
import com.example.cspart.models.Input
import com.example.cspart.models.Material
import com.example.cspart.models.MaterialInput
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenListInput : AppCompatActivity() {
    private var requestCode: String = ""
    private var currentPoint: Int = 0
    private var listArray: List<MaterialInput> = listOf ()
    private var textSearch: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_input)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        initSearchBar()
        bindingData()
    }

    private fun initSearchBar(){
        var searchView = findViewById<SearchView>(R.id.input_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                bindingListViewSearch(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                bindingListViewSearch(newText)
                textSearch = newText
                return false
            }
        })
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
                        listArray = listMaterial
                        if (textSearch.isNullOrEmpty()){
                            listView.adapter = AdapterMetarialInput(this@ScreenListInput,R.layout.material_input_detail, arrayList)
                            listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
//                            val itemAtPos  = adapterView.getItemAtPosition(i)
                                currentPoint = i
                                var itemMaterial = listMaterial?.get(i)
                                val intent = Intent(applicationContext, ScreentMaterialInputDetail::class.java)

                                intent.putExtra("materialCode", itemMaterial?.materialCode)
                                intent.putExtra("inputCode",itemMaterial?.inputCode)
                                intent.putExtra("item",itemMaterial)
                                startActivity(intent)
                            }
                            listView.setSelection(currentPoint)
                        } else {
                            bindingListViewSearch(textSearch)
                        }

                    }else{
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun bindingListViewSearch(textSearch: String){
        var filterQuery = ArrayList<MaterialInput>()
        for (item in listArray){
            if(item.orderNumber.toString().lowercase().contains(textSearch) || item.materialCode?.lowercase()
                    ?.contains(textSearch) == true || item.materialName?.lowercase()?.contains(textSearch) == true){
                filterQuery.add(item)
            }
        }
        var listView = findViewById<ListView>(R.id.lstMaterialDetail)
        if (filterQuery.isNullOrEmpty()) {
            return;
        }
        listView.adapter = AdapterMetarialInput(this@ScreenListInput,R.layout.material_input_detail, filterQuery)
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
            currentPoint = i
            var itemMaterial = filterQuery?.get(i)
            val intent = Intent(applicationContext, ScreentMaterialInputDetail::class.java)

            intent.putExtra("materialCode", itemMaterial?.materialCode)
            intent.putExtra("inputCode",itemMaterial?.inputCode)
            intent.putExtra("item",itemMaterial)
            startActivity(intent)
        }
        listView.setSelection(currentPoint)
    }

    override fun onResume() {
        super.onResume()
        bindingData()
    }
}