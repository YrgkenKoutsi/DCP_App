package com.example.dcpfm_android_9339.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "claim_properties")
data class ClaimProperties (

        @SerializedName("jbId")
        @Expose
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "jbId") var jbId: Int,

        @SerializedName("jbPeril")
        @Expose
        @ColumnInfo(name = "jbPeril") var jbPeril: String

) {

}