package com.example.cspart.models

data class ExportWareHouseDataRequest(val requestCode:String?, val materialCode:String?,
                                      val quantity: Int?, val serialCode: List<String>?, val loginName:String?)
