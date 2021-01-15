package com.example.dcpfm_android_9339.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
        tableName = "login",
        foreignKeys = [
            ForeignKey(
                    entity = UserProperties::class,
                    parentColumns = ["id"], //TODO fix the relationship of foreign keys
                    childColumns = ["account_id"],
                    onDelete = CASCADE
            )
        ]
)
data class AuthToken (

        @PrimaryKey
        @ColumnInfo(name = "account_id")
        var account_id: Int? = null,

        @SerializedName("token")
        @Expose
        @ColumnInfo(name = "token")
        var token: String? = null,

        @SerializedName("id")
        @Expose
        @ColumnInfo(name = "id")
        var id: Int? = null

)
