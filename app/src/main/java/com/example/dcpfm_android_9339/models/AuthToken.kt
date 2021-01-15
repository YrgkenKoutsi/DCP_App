package com.example.dcpfm_android_9339.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Data class for saving authentication token locally for api-test.disastercare.co.uk
 * NOTES:
 * 1) local 'auth_token' table has foreign key relationship to 'login_properties' table through 'account' field (ID)
 *
 * Docs: https://api-test.disastercare.co.uk/api/v1/
 */



@Entity(
        tableName = "auth_token",
        foreignKeys = [
            ForeignKey(
                    entity = LoginProperties::class,
                    parentColumns = ["id"],
                    childColumns = ["account_id"],
                    onDelete = CASCADE
            )
        ]
)
data class AuthToken (

        @PrimaryKey
        @Expose
        @ColumnInfo(name = "account_id")
        var account_id: Int? = -1,

        @SerializedName("token")
        @Expose
        @ColumnInfo(name = "token")
        var token: String? = null
)
