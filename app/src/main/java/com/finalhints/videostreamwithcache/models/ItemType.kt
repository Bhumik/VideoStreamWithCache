package com.finalhints.videostreamwithcache.models

import android.os.Parcel
import android.os.Parcelable

data class ItemType(
        var title: String? = null,
        var description: String? = null,
        //var videoUrl: String = "https://socialcops.com/images/old/spec/home/header-img-background_video-1920-480.mp4"
        //var videoUrl: String = "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4"
        var videoUrl: String = "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_10mb.mp4"
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(videoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemType> {
        override fun createFromParcel(parcel: Parcel): ItemType {
            return ItemType(parcel)
        }

        override fun newArray(size: Int): Array<ItemType?> {
            return arrayOfNulls(size)
        }
    }

}