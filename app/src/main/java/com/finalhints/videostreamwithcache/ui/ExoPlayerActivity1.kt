package com.finalhints.videostreamwithcache.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.models.ItemType
import com.finalhints.videostreamwithcache.utils.DownloadUtil
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.android.synthetic.main.activity_exo_player.*

/**
 * activity to show case playing video from remote file using exoplayer with caching latest buffer of video
 * Note: it doesn't download full video in background
 */
class ExoPlayerActivity1 : AppCompatActivity() {
    companion object {
        private const val EXTRA_DATA_ITEM = "EXTRA_DATA_ITEM"
        private const val EXTRA_CURRENT_INDEX = "EXTRA_CURRENT_INDEX"

        fun startActivity(context: Context, item: ItemType) {
            val intent = Intent(context, ExoPlayerActivity1::class.java)
            intent.putExtra(EXTRA_DATA_ITEM, item)
            context.startActivity(intent)
        }
    }

    private val mPlayerView: PlayerView by lazy { findViewById<PlayerView>(R.id.exoPlayerView) }
    private val mIvRetry: ImageView by lazy { findViewById<ImageView>(R.id.ivRetry) }

    /**
     * object containing item information to play
     */
    private lateinit var mItemType: ItemType

    private var mPlayer: SimpleExoPlayer? = null

    private var mPlayWhenReady = true
    /**
     * cache to help reference
     */
    private var mSimpleCache: SimpleCache? = null

    /**
     * latest playback position from which to start playing video
     */
    private var playbackPosition: Long = 0

    private var mMediaSource: ExtractorMediaSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)
        setBundleProperties()

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        mIvRetry.setOnClickListener {
            mIvRetry.visibility = View.GONE
            prepare()
        }
    }

    /**
     * set bundle properties
     */
    private fun setBundleProperties() {
        mItemType = intent.getParcelableExtra(EXTRA_DATA_ITEM)
    }

    /**
     * save current playback position to bundle
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_CURRENT_INDEX, mPlayer?.currentPosition ?: playbackPosition)
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }


    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * release player to free resources and coder
     */
    private fun releasePlayer() {
        mPlayer?.let {
            playbackPosition = it.currentPosition
            mPlayWhenReady = it.playWhenReady
            it.release()
        }
        mSimpleCache?.release()
        mPlayer = null
    }

    /**
     * initialize player with default/latest configuration and start playing video
     */
    private fun initializePlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(), DefaultLoadControl())

        mPlayerView.player = mPlayer

        mPlayer?.apply {
            this.playWhenReady = mPlayWhenReady
            seekTo(playbackPosition)
        }


        mSimpleCache = SimpleCache(DownloadUtil.getCacheFolder(this, "exo1"), NoOpCacheEvictor())

        val uri = Uri.parse(mItemType.videoUrl)

        val cacheFactory = CacheDataSourceFactory(mSimpleCache, DefaultDataSourceFactory(this, "videoStreamDemo1"))

        /* instance of mediasource with input cache data source */
        mMediaSource = ExtractorMediaSource.Factory(cacheFactory).createMediaSource(uri)

        mPlayer?.addListener(mPlayerListener)

        mPlayer?.prepare(mMediaSource, false, false)
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
            mIvRetry.visibility = View.VISIBLE
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
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_READY && mPlayerView.layoutParams.height != FrameLayout.LayoutParams.WRAP_CONTENT) {
                mPlayerView.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            }
        }
    }

}
