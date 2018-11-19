package com.example.rajiv.dictionary

import android.os.Parcel
import android.os.Parcelable


data class DefWithCategory(var def: String, val category: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(def)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefWithCategory> {
        override fun createFromParcel(parcel: Parcel): DefWithCategory {
            return DefWithCategory(parcel)
        }

        override fun newArray(size: Int): Array<DefWithCategory?> {
            return arrayOfNulls(size)
        }
    }
}
