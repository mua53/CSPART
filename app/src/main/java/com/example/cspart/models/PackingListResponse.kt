package com.example.cspart.models

data class PackingListResponse(
    var status: Int?,
    var message: String?,
    var packingListId: Int?,
    var cno: String?,
    var dateRequest: String?,
    var material:List<MaterialInput>
)
