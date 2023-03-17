package com.example.cspart.models

data class ReportDetailResponse(
    var id: Int?,
    var code: String?,
    var date: String?,
    var purpose: String?,
    var material: List<MaterialInput>
)
