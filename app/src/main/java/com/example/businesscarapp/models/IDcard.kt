package com.example.businesscarapp.models

data class IDcard(
    var name: String = "",
    var studentId: String = "",
    var school: String = "",
    var department: String = "",
    var description: String = ""
) {
    constructor() : this("", "", "", "", "")
}

