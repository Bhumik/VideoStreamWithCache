package com.finalhints.videostreamwithcache.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.models.ItemType
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_exo_player.*

/**
 * activity to show case playing video from remote file using exoplayer
 * Note: it doesn't allow caching of video
 */
class ExoPlayerActivity0 : AppCompatActivity() {
    companion object {
        private const val EXTRA_DATA_ITEM = "EXTRA_DATA_ITEM"
        private const val EXTRA_CURRENT_INDEX = "EXTRA_CURRENT_INDEX"

        fun startActivity(context: Context, item: ItemType) {
            val intent = Intent(context, ExoPlayerActivity0::class.java)
            intent.putExtra(EXTRA_DATA_ITEM, item)
            context.startActivity(intent)
        }
    }


    private val mPlayerView: PlayerView by lazy { findViewById<PlayerView>(R.id.exoPlayerView) }
    private lateinit var mItemType: ItemType

    private var player: SimpleExoPlayer? = null
    private var mPlayWhenReady = true

    /**
     * latest window index from which to start playing video
     */
    private var windowIndex: Int = 0
    /**
     * latest playback position from which to resume playing video
     */
    private var playbackPosition: Long = 0

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
        outState.putLong(EXTRA_CURRENT_INDEX, player?.currentPosition ?: playbackPosition)
    }


    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }


    /**
     * release player to free resources and coder
     */
    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            windowIndex = it.currentWindowIndex
            mPlayWhenReady = it.playWhenReady
            it.release()
        }
        Toast.makeText(this, "Window:$windowIndex -Seek:${player?.currentPosition}", Toast.LENGTH_SHORT).show()
        player = null
    }

    /**
     * initialize player with default/latest configuration and start playing video
     */
    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(), DefaultLoadControl())

        mPlayerView.player = player

        player?.apply {
            this.playWhenReady = mPlayWhenReady
            seekTo(windowIndex, playbackPosition)
        }

        val uri = Uri.parse(mItemType.videoUrl)
        val mediaSource = buildMediaSource(uri)
        player?.prepare(mediaSource, false, false)
    }

    /**
     * return medita source
     */
    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("videoStreamDemo0")).createMediaSource(uri)
    }

}
