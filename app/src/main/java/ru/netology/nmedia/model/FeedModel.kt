package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Album
import ru.netology.nmedia.dto.Track

data class FeedModel(
    val album: Album? = null,
    val activeTrack : Track? = null,
    val isAlbumPlaying : Boolean? = null,
    val empty: Boolean = false,
    val error: Boolean = false,
    val loading: Boolean = false,
)
