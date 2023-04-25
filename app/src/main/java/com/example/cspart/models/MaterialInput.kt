package com.example.cspart.models

data class MaterialInput(
    var materialID: String?,
    var materialCode: String?,
    var materialName: String?,
    var typeMaterial: Boolean?,
    var typeMaterialText: String?,
    var quantityRequest: Int?,
    var quantityInput: Int?,
    var totalQuantityExport: Int?,
    var quantityReal: Int?,
    var quantity: Int?,
    var serialNumber: List<String>,
    var inputCode:String?,
    var areaCode:String?,
    var areaName:String?,
    var lstArea: List<Area>?,
    var detail: List<Area>?,
    var lstSerialDetail: List<SerialDetail>?,
    var quantityInArea:Int?,
    var orderNumber:Int?
): java.io.Serializable
