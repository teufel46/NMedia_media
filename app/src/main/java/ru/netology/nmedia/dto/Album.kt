package ru.netology.nmedia.dto

data class Album(
    val id : Long,
    val title : String,
    val subtitle : String,
    val artist : String,
    val published : String,
    val genre : String,
    val tracks : List<Track>? = emptyList()
)

data class Track(
    val id: Long,
    val file: String,
    var isPlaying : Boolean = false
)
