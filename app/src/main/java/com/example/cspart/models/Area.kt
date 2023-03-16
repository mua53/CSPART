package com.example.cspart.models

data class Area(
    var materialCode: String?,
    var areaCode:String?,
    var areaName:String?,
    var quantityInArea: Int?,
    var quantityExported: Int?,
    var totalQuantity: Int?
): java.io.Serializable
