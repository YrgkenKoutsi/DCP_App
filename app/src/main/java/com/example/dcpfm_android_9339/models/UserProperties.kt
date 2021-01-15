package com.example.dcpfm_android_9339.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_properties")
data class UserProperties(

        @SerializedName("id")
        @Expose
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        var id: Int,

        @SerializedName("username")
        @Expose
        @ColumnInfo(name = "username")
        var username: String
)