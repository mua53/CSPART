package com.example.cspart.models

data class ReportResponse(
    var status: Int?,
    var message: String?,
    var data: ReportDetailResponse?
)
