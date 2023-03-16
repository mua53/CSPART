package com.example.cspart.models

data class PackListRequest(
    var code : String?,
    var cno: String?,
    var materialCode: String?,
    var areaCode: String?,
    var quantity: Int?,
    var serialCode: List<String>?,
    var loginName: String?
)
