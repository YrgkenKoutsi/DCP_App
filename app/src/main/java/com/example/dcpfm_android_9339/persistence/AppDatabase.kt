package com.example.dcpfm_android_9339.persistence

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.models.UserProperties

@Database(entities = [AuthToken::class, UserProperties::class], version = 8)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getUserPropertiesDao(): UserPropertiesDao

    abstract fun getClaimPropertiesDao(): ClaimPropertiesDao

    abstract fun getVisitPropertiesDao(): VisitPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}