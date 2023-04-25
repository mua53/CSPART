package com.example.cspart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.cspart.R.id.lstMaterialExportDetail
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.AdapterMaterial
import com.example.cspart.models.AdapterMaterialExport
import com.example.cspart.models.Material
import com.example.cspart.models.MaterialInput
import com.example.cspart.models.PackingListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenListExportWarehouse : AppCompatActivity() {
    private var exportWarehouseCode: String = ""
    private var currentPoint: Int = 0
    private var listArray: List<MaterialInput> = listOf ()
    private var textSearch: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_export_warehouse)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        initSearchBar()
        bindingData()
    }

    private fun initSearchBar(){
        var searchView = findViewById<SearchView>(R.id.export_ware_house_search)
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

    private fun bindingListViewSearch(textSearch: String){
        var filterQuery = ArrayList<MaterialInput>()
        var search = textSearch.lowercase()
        for (item in listArray){
            if(item.orderNumber.toString().lowercase().contains(search) || item.materialCode?.lowercase()
                    ?.contains(search) == true || item.materialName?.lowercase()?.contains(search) == true){
                filterQuery.add(item)
            }
        }
        if (filterQuery.isNullOrEmpty()) {
            return;
        }
        var listView = findViewById<ListView>(R.id.lstMaterialExportDetail)
        listView.adapter = AdapterMaterialExport(this@ScreenListExportWarehouse,R.layout.material_input_detail, filterQuery)
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
            currentPoint = i
            var itemMaterial = filterQuery?.get(i)
            val intent = Intent(applicationContext, ScreenMaterialExportDetail::class.java)

            intent.putExtra("materialCode", itemMaterial?.materialCode)
            intent.putExtra("inputCode",itemMaterial?.inputCode)
            intent.putExtra("item",itemMaterial)
            startActivity(intent)
        }
        listView.setSelection(currentPoint)
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
                        listArray = listMaterial
                        if (textSearch.isNullOrEmpty()) {

                            listView.adapter = AdapterMaterialExport(this@ScreenListExportWarehouse,R.layout.material_input_detail, arrayList)
                            listView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, i:Int, l:Long ->
                                currentPoint = i
                                var itemMaterial = listMaterial?.get(i)
                                val intent = Intent(applicationContext, ScreenMaterialExportDetail::class.java)

                                intent.putExtra("materialCode", itemMaterial?.materialCode)
                                intent.putExtra("inputCode",itemMaterial?.inputCode)
                                intent.putExtra("item",itemMaterial)
                                startActivity(intent)
                            }
                            listView.setSelection(currentPoint)
                        }else {
                            bindingListViewSearch(textSearch)
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