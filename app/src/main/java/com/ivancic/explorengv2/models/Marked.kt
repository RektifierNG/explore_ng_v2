package com.ivancic.explorengv2.models

data class Marked ( var image: String?= null, var upimage : String?=null,
               var lon: String?=null, var title : String?=null,
               var type: String?=null, var noImages: String?=null,
              var lat: String?=null, var desc: String?= null, var descEng: String?= null,
                    var id:Int=0, var detailedDesc:String="", var detailedDescEng:String=""
)