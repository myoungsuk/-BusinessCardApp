package com.example.businesscarapp

data class IDcard(
    var name: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var uid: String = "",
    var studentId: String = "",
    var school: String = "",
    var department: String = ""
) {
    constructor() : this("", "", "", "", "", "", "")
}
