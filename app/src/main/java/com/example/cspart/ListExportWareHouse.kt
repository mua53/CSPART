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
import com.example.cspart.models.ExportWareHouseResponse
import com.example.cspart.models.Material
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListExportWareHouse : AppCompatActivity() {
    private var requestCode: String = ""
    private var currentPoint: Int = 0
    private var listArray: List<Material> = listOf ()
    private var textSearch: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_export_ware_house)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.rtcimg)
        supportActionBar!!.title = "  CS PART"
        requestCode = intent.getStringExtra("requestCode").toString()
        bindingData(this)
        initSearchBar()
    }

    override fun onResume() {
        super.onResume()
        bindingData(this)
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
        var listExportWareHouse = this
        var filterQuery = ArrayList<Material>()
        for (item in listArray){
            if(item.orderNumber.toString().lowercase().contains(textSearch) || item.materialCode?.lowercase()
                    ?.contains(textSearch) == true || item.materialName?.lowercase()?.contains(textSearch) == true){
                filterQuery.add(item)
            }
        }
        if (filterQuery.isNullOrEmpty()) {
            return;
        }
        var listView = findViewById<ListView>(R.id.lstMaterialDetail)
        listView.adapter = AdapterMaterial(listExportWareHouse,R.layout.material_detail, filterQuery)
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view:View, i:Int, l:Long ->
            currentPoint = i
            var itemMaterial = filterQuery[i]
            val intent = Intent(applicationContext, MaterialDetail::class.java)
            intent.putExtra("requestCode",itemMaterial.requestCode)
            intent.putExtra("materialCode",itemMaterial.materialCode)
            intent.putExtra("item",itemMaterial)
            startActivity(intent)
        }
        listView.setSelection(currentPoint)
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
                        for (item in listMaterial) {
                            item.requestCode = requestCode
                        }
                        listArray = listMaterial
                        var arrayList = ArrayList(listMaterial)
                        if (textSearch.isNullOrEmpty()) {
                            listView.adapter = AdapterMaterial(listExportWareHouse,R.layout.material_detail, arrayList)
                            listView.setOnItemClickListener { adapterView: AdapterView<*>, view:View, i:Int, l:Long ->
//                            val itemAtPos  = adapterView.getItemAtPosition(i)
                                currentPoint = i
                                var itemMaterial = listMaterial[i]
                                val intent = Intent(applicationContext, MaterialDetail::class.java)
                                intent.putExtra("requestCode",itemMaterial.requestCode)
                                intent.putExtra("materialCode",itemMaterial.materialCode)
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
}