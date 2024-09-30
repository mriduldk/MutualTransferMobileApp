package com.codingstudio.mutualtransfer.model.search

import android.os.Parcel
import android.os.Parcelable


enum class SearchedType() : Parcelable {
    DISTRICT,
    BLOCK,
    SCHOOL;

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<SearchedType> {
            override fun createFromParcel(parcel: Parcel): SearchedType {
                return values()[parcel.readInt()]
            }

            override fun newArray(size: Int): Array<SearchedType?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

}