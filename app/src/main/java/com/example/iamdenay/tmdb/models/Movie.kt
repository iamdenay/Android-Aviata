package com.example.iamdenay.tmdb.models

data class Movie(var id:Int ? = null,
                 var title:String? = "",
                 var originalTitle:String? = "",
                 var score:Float = 0f,
                 var votes:Int? = 0,
                 var posterPath:String? = null,
                 var overview:String? = "",
                 var budget:Int? = 0,
                 var releaseDate:String? = null,
                 var genres:ArrayList<Genre>? = null,
                 var duration:Int? = 0,
                 var actors:ArrayList<Actor>? = null,
                 var similar:ArrayList<Movie>? = null) {


}