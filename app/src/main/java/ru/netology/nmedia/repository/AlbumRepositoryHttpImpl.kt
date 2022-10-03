package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.dto.Album
import java.util.concurrent.TimeUnit

class AlbumRepositoryHttpImpl : AlbumRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<Album>() {}

    override fun getAlbum(): Album {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/album.json")
            .build()
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }
}