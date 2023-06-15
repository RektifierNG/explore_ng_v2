package com.ivancic.explorengv3.models

data class User(
    var name: String="", var uid: String="",
    var pic: Int=0, var lastCollectedLocation: String="",
    var avgPointsQuiz:Double=0.0, var collectedLocationNumber: Int=0,
    var totalPoints: Double =0.0, var numberOfQuizzes:Int=0,
    var totalQuizPoint: Int =0, var collectedLocations: ArrayList<Int> = arrayListOf(0),
    var userNo: Int =0, var collectedLocationDates: ArrayList<String> = arrayListOf("")
)
