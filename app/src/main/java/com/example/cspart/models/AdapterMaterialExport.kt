package com.example.cspart.models

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cspart.R

class AdapterMaterialExport(context: Context, layoutInt: Int, var arrayList: List<MaterialInput>) : ArrayAdapter<MaterialInput>(context,
    R.layout.material_detail, arrayList) {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.material_detail,null)

        val name: TextView = view.findViewById(R.id.txtMaterialName)
        val code: TextView = view.findViewById(R.id.txtMaterialCode)
        var total: TextView = view.findViewById(R.id.txtTotal)

        name.text = arrayList[position].materialName
        code.text = arrayList[position].materialCode

        total.text = arrayList[position].totalQuantityExport.toString() + "/" + arrayList[position].quantityRequest.toString()

        if (arrayList[position].totalQuantityExport == arrayList[position].quantityRequest){
            total.setTextColor(Color.parseColor("#FF5D9503"))
        } else {
            total.setTextColor(Color.parseColor("#FF0000"))
        }

        return view
    }
}