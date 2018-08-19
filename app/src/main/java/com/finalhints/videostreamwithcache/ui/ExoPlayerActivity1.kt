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
import com.finalhints.videostreamwithcache.utils.DownloadUtil
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
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

        fun startActivity(context: Context, item: ItemType) {
            val intent = Intent(context, ExoPlayerActivity1::class.java)
            intent.putExtra(EXTRA_DATA_ITEM, item)
            context.startActivity(intent)
        }
    }


    private val mPlayerView: PlayerView by lazy { findViewById<PlayerView>(R.id.exoPlayerView) }
    private lateinit var mItemType: ItemType

    private var player: SimpleExoPlayer? = null
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
        mSimpleCache?.release()
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


        mSimpleCache = SimpleCache(DownloadUtil.getCacheFolder(this, "exo1"), NoOpCacheEvictor());

        val uri = Uri.parse(mItemType.videoUrl)

        val cacheFactory = CacheDataSourceFactory(mSimpleCache, DefaultDataSourceFactory(this, "videoStreamDemo1"))

        /* instance of mediasource with input cache data source */
        mMediaSource = ExtractorMediaSource.Factory(cacheFactory).createMediaSource(uri)

//        setListener()
        player?.prepare(mMediaSource, false, false)
    }

    /*  private fun setListener() {
          player?.addListener(object : Player.EventListener{
              override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

              }

              override fun onSeekProcessed() {

              }

              override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

              }

              override fun onPlayerError(error: ExoPlaybackException?) {
                  Log.d("TEST", "===== onPlayerError - message: "+error?.message);
                  Log.d("TEST", "===== onPlayerError - sourceException: "+error?.sourceException);
                  Log.d("TEST", "===== onPlayerError - type: "+error?.type);
                  Log.d("TEST", "===== onPlayerError - localizedMessage: "+error?.localizedMessage);
                  Log.d("TEST", "===== onPlayerError - cause: "+error?.cause);
                  error?.printStackTrace()
                  if(error?.sourceException is HttpDataSource.HttpDataSourceException){

                  }
              }

              override fun onLoadingChanged(isLoading: Boolean) {
                  Log.d("TEST", "===== onLoadingChanged - isLoading: $isLoading");

              }

              override fun onPositionDiscontinuity(reason: Int) {

              }

              override fun onRepeatModeChanged(repeatMode: Int) {

              }

              override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

              }

              override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

              }

              override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                  val state = when(playbackState){
                      Player.STATE_BUFFERING -> "STATE_BUFFERING"
                      Player.STATE_ENDED -> "STATE_ENDED"
                      Player.STATE_IDLE -> "STATE_IDLE"
                      Player.STATE_READY -> "STATE_READY"
                      else -> "UNKNOWN"
                  }
                  Log.d("TEST", "===== onPlayerStateChanged - playWhenReady: $playWhenReady - playbackState: $playbackState - state: $state ==");
              }

          })

          player.

      }*/


}
