package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Track
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.AlbumRepository
import ru.netology.nmedia.repository.AlbumRepositoryHttpImpl
import java.io.IOException
import kotlin.concurrent.thread

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AlbumRepository = AlbumRepositoryHttpImpl()

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    init {
        loadAlbum()
    }

    fun loadAlbum() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val album = repository.getAlbum()
                FeedModel(album = album)
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun playAlbum(activeTrack: Track?) {
        val tracks = _data.value?.album?.tracks?.map { track ->
            if (track == activeTrack) track.copy(isPlaying = true)
            else track.copy(isPlaying = false)
        }
        val album = _data.value?.album?.copy(tracks = tracks)
        _data.postValue(FeedModel(album = album, activeTrack = activeTrack, isAlbumPlaying = true))
    }

    fun pauseAlbum(activeTrack: Track?) {
        val tracks = _data.value?.album?.tracks?.map { track -> track.copy(isPlaying = false) }
        val album = _data.value?.album?.copy(tracks = tracks)
        _data.postValue(FeedModel(album = album, activeTrack = activeTrack, isAlbumPlaying = true))
    }

    fun playTrack(activeTrack: Track?) {
        val tracks = _data.value?.album?.tracks?.map { track ->
            if (track == activeTrack) track.copy(isPlaying = true)
            else track.copy(isPlaying = false)
        }
        val album = _data.value?.album?.copy(tracks = tracks)
        _data.postValue(FeedModel(album = album, activeTrack = activeTrack, isAlbumPlaying = false))
    }

    fun pauseTrack(activeTrack: Track?) {
        val tracks = _data.value?.album?.tracks?.map { track -> track.copy(isPlaying = false) }
        val album = _data.value?.album?.copy(tracks = tracks)
        _data.postValue(FeedModel(album = album, activeTrack = activeTrack, isAlbumPlaying = false))
    }

    fun findActiveTrack(): Track? = _data.value?.activeTrack

    fun findFirst(): Track? = _data.value?.album?.tracks?.first()

    fun nextTrack(): Track? {
        val activeTrack = _data.value?.activeTrack
        val tracks = data.value?.album?.tracks
        val maxIndex = tracks?.lastIndex
        var currentIndex = -1
        if (tracks != null) {
            for ((index, element) in tracks.withIndex()) {
                if (element.id == activeTrack?.id) {
                    currentIndex = index
                }
            }

        }
        val nextIndex = if (currentIndex == maxIndex) 0 else currentIndex + 1
        return tracks?.get(nextIndex)
    }

    fun checkPlayAlbum(): Boolean? = _data.value?.isAlbumPlaying
}