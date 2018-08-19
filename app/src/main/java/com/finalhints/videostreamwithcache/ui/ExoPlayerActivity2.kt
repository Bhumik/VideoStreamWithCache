package com.finalhints.videostreamwithcache.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.finalhints.videostreamwithcache.R
import com.finalhints.videostreamwithcache.models.ItemType
import com.finalhints.videostreamwithcache.service.ExoDownloadService
import com.finalhints.videostreamwithcache.utils.DownloadUtil
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import kotlinx.android.synthetic.main.activity_exo_player.*

/**
 * activity to show case playing video from remote file using exoplayer with caching latest buffer of video
 * with downloading full video in background
 */
class ExoPlayerActivity2 : AppCompatActivity() {
    companion object {
        private const val EXTRA_DATA_ITEM = "EXTRA_DATA_ITEM"

        fun startActivity(context: Context, item: ItemType) {
            val intent = Intent(context, ExoPlayerActivity2::class.java)
            intent.putExtra(EXTRA_DATA_ITEM, item)
            context.startActivity(intent)
        }
    }


    private val mPlayerView: PlayerView by lazy { findViewById<PlayerView>(R.id.exoPlayerView) }
    private lateinit var mItemType: ItemType

    private var player: SimpleExoPlayer? = null
    private var mPlayWhenReady = true

    /**
     * latest playback position from which to start playing video
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
            mPlayWhenReady = it.playWhenReady
            it.release()
        }
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
            seekTo(playbackPosition)
        }

        val mSimpleCache = DownloadUtil.getCache(this)

        val uri = Uri.parse(mItemType.videoUrl)

        val cacheFactory = CacheDataSourceFactory(mSimpleCache, DefaultDataSourceFactory(this, "videoStreamDemo1"))

        /* instance of mediasource with input cache data source */
        val mediaSource = ExtractorMediaSource.Factory(cacheFactory).createMediaSource(uri)

        /* downlaod service which download full file*/
        val progressiveDownloadAction = ProgressiveDownloadAction(uri, false, null, null);
        ExoDownloadService.startService(this, ExoDownloadService::class.java, progressiveDownloadAction, true);

        player?.prepare(mediaSource, false, false)
    }

}
