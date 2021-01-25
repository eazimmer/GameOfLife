package com.example.project2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cell(var row: Int,
                var column: Int,
                var status: Boolean,
                ) : Parcelable {

    // Flip the status of the cell
    fun changeStatus() {
        this.status = !status
    }

    // Assign status for the cell
    fun assignStatus(status: Boolean) {
        this.status = status
    }
}