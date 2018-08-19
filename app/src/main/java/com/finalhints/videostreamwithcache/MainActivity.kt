package com.finalhints.videostreamwithcache

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.finalhints.videostreamwithcache.adapter.ItemListAdapter
import com.finalhints.videostreamwithcache.models.ItemType
import com.finalhints.videostreamwithcache.ui.ExoPlayerActivity0
import com.finalhints.videostreamwithcache.ui.ExoPlayerActivity1
import com.finalhints.videostreamwithcache.ui.ExoPlayerActivity2
import com.finalhints.videostreamwithcache.utils.DownloadUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mEntityList = ArrayList<ItemType>()

    private lateinit var mAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        rcvItem.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter = ItemListAdapter(this, mEntityList)
        rcvItem.adapter = mAdapter

        mAdapter.setActionListener(object : ItemListAdapter.ActionListener {
            override fun onItemClick(position: Int, item: ItemType) {
                openVideoScreen(position, item)
            }
        })
    }

    private fun openVideoScreen(position: Int, item: ItemType) {
        when (position) {
            0 -> ExoPlayerActivity0.startActivity(this, item)
            1 -> ExoPlayerActivity1.startActivity(this, item)
            2 -> ExoPlayerActivity2.startActivity(this, item)
        }
    }

    private fun getData() {
        mEntityList.add(ItemType("VideoStream without offline cache", "Simple example of playing MP4 video without any type of offline caching"))
        mEntityList.add(ItemType("VideoStream with offline cache", "Example of playing MP4 video with offline caching - it doesn't go for downloading whole file if you have not watched till end"))
        mEntityList.add(ItemType("VideoStream with full offline cache", "Example of playing MP4 video with full video offline caching - it downloads full video in background"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_mainactivity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_clearcache -> {
                DownloadUtil.clearCache(this)
                Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
