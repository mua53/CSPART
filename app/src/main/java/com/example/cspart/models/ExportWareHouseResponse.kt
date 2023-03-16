package com.example.cspart.models

data class ExportWareHouseResponse(val statusCode:Int,
                                   val message:String,
                                   val exportWarehouseId: Int,
                                   val requestCode:String,
                                   val res:String,
                                   val reason:String, //Ly do tao phieu
                                   val arivalTime:String,
                                   val listMaterial:List<Material>)
