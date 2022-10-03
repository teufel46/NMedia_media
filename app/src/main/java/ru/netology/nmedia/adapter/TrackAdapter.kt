package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardTrackBinding
import ru.netology.nmedia.dto.Track
import ru.netology.nmedia.media.MediaLifecycleObserver

interface OnInteractionListener {
    fun onPlay(track: Track) {}
    fun onPause(track: Track) {}
}


class TrackAdapter(
    private val mediaObserver: MediaLifecycleObserver,
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = CardTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, mediaObserver, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

class TrackViewHolder(
    private val binding: CardTrackBinding,
    private val mediaObserver: MediaLifecycleObserver,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.apply {
            trackFile.text = track.file
            buttonPlayPause.isChecked = track.isPlaying
            buttonPlayPause.setOnClickListener {
                // кнопку нажали, хотим поставить на паузу
                if (!buttonPlayPause.isChecked) {
                    mediaObserver.player?.pause()
                    onInteractionListener.onPause(track)
                } else
                    if (track.isPlaying) {
                        mediaObserver.player?.start()
                        onInteractionListener.onPlay(track)
                        buttonPlayPause.isChecked = true
                    } else {
                        val sourceFile = BuildConfig.BASE_URL + "/" + track.file
                        mediaObserver.apply {
                            player?.reset()
                            player?.setDataSource(sourceFile)
                        }.play()
                        onInteractionListener.onPlay(track)
                        buttonPlayPause.isChecked = true
                    }
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}
