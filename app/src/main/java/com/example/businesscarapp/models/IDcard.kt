package com.example.businesscarapp.models

data class IDcard(
    var uid: String = "",
    var name: String = "",
    var studentId: String = "",
    var school: String = "",
    var department: String = "",
    var description: String = "",
    var createdAt : Long,
    var profileImageUrl: String = ""
) {
    constructor() : this("", "", "", "", "", "", 0, "")
}
