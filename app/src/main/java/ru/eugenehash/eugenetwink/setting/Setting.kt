package ru.eugenehash.eugenetwink.setting

import android.os.Parcel
import android.os.Parcelable

class Setting : Parcelable {

    var bright: Int = 0
    var power: Boolean = false
    var random: Boolean = false
    var autoplay: Boolean = false

    constructor()

    private constructor(parcel: Parcel) {
        bright = parcel.readInt()
        power = parcel.readInt() == 1
        random = parcel.readInt() == 1
        autoplay = parcel.readInt() == 1
    }

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bright)
        parcel.writeInt(if (power) 1 else 0)
        parcel.writeInt(if (random) 1 else 0)
        parcel.writeInt(if (autoplay) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Setting?> {

        override fun createFromParcel(parcel: Parcel) = Setting(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Setting?>(size)
    }
}