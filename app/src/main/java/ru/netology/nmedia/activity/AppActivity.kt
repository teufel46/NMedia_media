package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.TrackAdapter
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.dto.Track
import ru.netology.nmedia.media.MediaLifecycleObserver
import ru.netology.nmedia.viewmodel.AlbumViewModel


class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: AlbumViewModel by viewModels()

        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)

        val adapterTrack = TrackAdapter(mediaObserver, object : OnInteractionListener {
            override fun onPlay(track: Track) {
                viewModel.playTrack(track)
                binding.buttonPlayPause.isChecked = false
            }

            override fun onPause(track: Track) {
                viewModel.pauseTrack(track)
                binding.buttonPlayPause.isChecked = false
            }
        })

        binding.list.adapter = adapterTrack

        viewModel.data.observe(this)
        { feedModel ->
            binding.progress.isVisible = feedModel.loading
            if (feedModel.error) {
                Snackbar.make(binding.root, R.string.albumError, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) { viewModel.loadAlbum() }
                    .show()
            }
            binding.group.isVisible = true
            adapterTrack.submitList(feedModel.album?.tracks)
            binding.albumTitle.text = feedModel.album?.title
            binding.albumArtist.text = feedModel.album?.artist
            binding.published.text = feedModel.album?.published
            binding.genre.text = feedModel.album?.genre
        }

        binding.buttonPlayPause.setOnClickListener {
            with(mediaObserver) {
                if (!binding.buttonPlayPause.isChecked) {
                    player?.pause()
                    viewModel.pauseAlbum(viewModel.findActiveTrack())
                    binding.buttonPlayPause.isChecked = false
                } else {
                    if (viewModel.checkPlayAlbum() == true) {
                        val activeTrack = viewModel.findActiveTrack()
                        player?.start()
                        viewModel.playAlbum(activeTrack)
                        binding.buttonPlayPause.isChecked = true
                        player?.setOnCompletionListener {
                            player?.reset()
                            val activeTrack = viewModel.nextTrack()
                            val sourceFile = BuildConfig.BASE_URL + "/" + activeTrack?.file
                            player?.setDataSource(sourceFile)
                            play()
                            viewModel.playAlbum(activeTrack)
                        }
                    } else {
                        player?.reset()
                        val activeTrack = viewModel.findFirst()
                        val sourceFile = BuildConfig.BASE_URL + "/" + activeTrack?.file
                        player?.setDataSource(sourceFile)
                        play()
                        viewModel.playAlbum(activeTrack)
                        binding.buttonPlayPause.isChecked = true
                        player?.setOnCompletionListener {
                            player?.reset()
                            val activeTrack = viewModel.nextTrack()
                            val sourceFile = BuildConfig.BASE_URL + "/" + activeTrack?.file
                            player?.setDataSource(sourceFile)
                            play()
                            viewModel.playAlbum(activeTrack)
                        }
                    }
                }
            }
        }
    }
}