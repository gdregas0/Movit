package com.example.project00.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.project00.R

import android.net.Uri

import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), Player.EventListener {
    /*
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING)
                progressBar.visibility = View.VISIBLE
            else if (playbackState == Player.STATE_READY)
                progressBar.visibility = View.INVISIBLE
        }

        private lateinit var simpleExoplayer: SimpleExoPlayer
        private var playbackPosition = 0L
        private val dashUrl =
            "http://www.youtube.com/get_video_info?&video_id=mJUvc5sq-VE&&el=info&ps=default&eurl=&gl=US&hl=en"

        private val bandwidthMeter by lazy {
            DefaultBandwidthMeter()
        }
        private val adaptiveTrackSelectionFactory by lazy {
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        }

        private fun initializeExoplayer() {
            simpleExoplayer = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultRenderersFactory(context),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
            )

            prepareExoplayer()

            exoPlayerView.player = simpleExoplayer
            simpleExoplayer.seekTo(playbackPosition)
            simpleExoplayer.playWhenReady = true
            simpleExoplayer.addListener(this)
        }

        private fun releaseExoplayer() {
            playbackPosition = simpleExoplayer.currentPosition
            simpleExoplayer.release()
        }

        private fun buildMediaSource(uri: Uri): MediaSource {
            val dataSourceFactory = DefaultHttpDataSourceFactory("ua", bandwidthMeter)
            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
            return DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null)
        }

        private fun prepareExoplayer() {
            val uri = Uri.parse(dashUrl)
            val mediaSource = buildMediaSource(uri)
            simpleExoplayer.prepare(mediaSource)
        }
    */
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onStart() {
        super.onStart()

        //initializeExoplayer()
    }

    override fun onStop() {

        //releaseExoplayer()
        super.onStop()
    }
}