package com.ivancic.explorengv3.models

data class Question(
    val question: String="", val optionA:String="",
    val optionB:String="",val optionC:String="",
    val optionD:String="", val type : Int=0,
    val imageOfMarker: String="", val answer:Int=0
)
