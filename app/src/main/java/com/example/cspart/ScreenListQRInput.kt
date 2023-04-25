package com.example.cspart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.cspart.models.AdapterMetarialInput
import com.example.cspart.models.MaterialInput


class ScreenListQRInput : AppCompatActivity() {

    private var listArray: List<String> = listOf ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_list_qrinput)
        initSearchBar()
        bindingData()
    }

    private fun bindingData(){
        var material = intent.getSerializableExtra("materialDetail") as MaterialInput?
        var listDetail = material?.lstSerialDetail
        var lstQRCodeString = ArrayList<String>();
        if (listDetail != null) {
            for (item in listDetail) {
                var stringContent = item.areaCode + " - " +item.serialNumber
                lstQRCodeString.add(stringContent)
            }
        }
        listArray = lstQRCodeString
        var listView = findViewById<ListView>(R.id.lstQRCodeInput)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, lstQRCodeString
        )
        listView.adapter = adapter
    }

    private fun initSearchBar(){
        var searchView = findViewById<SearchView>(R.id.qrcode_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                bindingListViewSearch(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                bindingListViewSearch(newText)
                return false
            }
        })
    }

    private fun bindingListViewSearch(textSearch: String){
        var filterQuery = ArrayList<String>()
        var search = textSearch.lowercase()
        for (item in listArray){
            if(item.lowercase().contains(search)){
                filterQuery.add(item)
            }
        }
        if (filterQuery.isNullOrEmpty()) {
            return;
        }
        var listView = findViewById<ListView>(R.id.lstQRCodeInput)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, filterQuery
        )
        listView.adapter = adapter
    }
}