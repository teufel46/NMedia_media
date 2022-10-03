package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Album

interface AlbumRepository {
  fun getAlbum(): Album
}

