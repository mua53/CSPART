package com.example.cspart.models

data class Input(
    var status: Int?,
    var message: String?,
    var inputId: Int?,
    var inputCode: String?,
    var material: List<MaterialInput>?,
    var area: List<Area>?,
    var serialDetail: List<SerialDetail>?
): java.io.Serializable
