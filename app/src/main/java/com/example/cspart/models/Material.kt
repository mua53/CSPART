package com.example.cspart.models

import java.io.Serializable

data class Material(
    var materialId: Int?,
    var materialCode: String?,
    var materialName: String?,
    var typeMaterial:Boolean?,
    var typeMaterialText:String,
    var warehouseCode: String?,
    var warehouseName: String?,
    var serialCode: List<String>,
    var quantityRequest: Int?,
    var quantityGet: Int?,
    var stt: Int?,
    var materialImage: List<MaterialImage>,
    var requestCode:String?) : Serializable
