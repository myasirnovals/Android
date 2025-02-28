package com.example.bluearchiveacademy.student

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    var name: String = "",
    var academy: String = "",
    var photo: String = "",
    var overview: String = "",
    var detail: String = ""
) : Parcelable