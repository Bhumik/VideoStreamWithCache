package com.finalhints.videostreamwithcache.ui

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.models.ItemType
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_exo_player.*

/**
 * activity to show case playing video from remote file using exoplayer
 * Note: it doesn't allow caching of video
 */
abstract class BaseExoPlayerActivity : AppCompatActivity() {
    companion object {
        public const val EXTRA_DATA_ITEM = "EXTRA_DATA_ITEM"
        public const val EXTRA_CURRENT_INDEX = "EXTRA_CURRENT_INDEX"
    }

    private val mPlayerView: PlayerView by lazy { findViewById<PlayerView>(R.id.exoPlayerView) }

    /**
     * object containing item information to play
     */
    private lateinit var mItemType: ItemType

    private var mPlayer: SimpleExoPlayer? = null

    private var mPlayWhenReady = true

    /**
     * latest window index from which to start playing video
     */
    private var windowIndex: Int = 0
    /**
     * latest playback position from which to resume playing video
     */
    private var playbackPosition: Long = 0

    private var mMediaSource: MediaSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)
        setBundleProperties()

        /* set values */
        tvTitle.text = mItemType.title ?: ""
        tvDescription.text = mItemType.description ?: ""
        tvSummary.text = mItemType.description ?: ""


        /* on horizontal layout, hide action and status bar to make full screen view while on verticle reset it */
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (savedInstanceState?.containsKey(EXTRA_CURRENT_INDEX) == true) {
            playbackPosition = savedInstanceState.getLong(EXTRA_CURRENT_INDEX, 0)
        }

        ivRetry.setOnClickListener {
            ivRetry.visibility = View.GONE
            prepare()
        }
    }

    /**
     * set bundle properties
     */
    private fun setBundleProperties() {
        mItemType = intent.getParcelableExtra(EXTRA_DATA_ITEM)
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_CURRENT_INDEX, mPlayer?.currentPosition ?: playbackPosition)
    }


    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }


    /**
     * release player to free resources and coder
     */
    abstract fun releasePlayer()

    /**
     * initialize player with default/latest configuration and start playing video
     */
    abstract fun initializePlayer()

    /**
     * return medita source
     */
    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("videoStreamDemo0")).createMediaSource(uri)
    }

    /**
     * retry to play media
     */
    private fun prepare() {
        mPlayer?.prepare(mMediaSource, false, false)
        mPlayerView.showController()
    }

    private val mPlayerListener = object : Player.EventListener {
        override fun onPlayerError(error: ExoPlaybackException?) {
            //in case of any error show retry button and retry media play on its click
            error?.printStackTrace()
            ivRetry.visibility = View.VISIBLE
            mPlayerView.hideController()
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
        override fun onSeekProcessed() {}
        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
        override fun onLoadingChanged(isLoading: Boolean) {}
        override fun onPositionDiscontinuity(reason: Int) {}
        override fun onRepeatModeChanged(repeatMode: Int) {}
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}
    }

}
