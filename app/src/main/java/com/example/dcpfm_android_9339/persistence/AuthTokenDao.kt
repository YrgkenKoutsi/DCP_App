package com.example.dcpfm_android_9339.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dcpfm_android_9339.models.AuthToken
import com.example.dcpfm_android_9339.models.LoginProperties

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthToken): Long

    @Query("UPDATE auth_token SET token = null WHERE account_id = :id")
    fun nullifyToken(id: Int): Int

    @Query("SELECT * FROM auth_token WHERE account_id = :id")
    suspend fun searchById(id: Int): LiveData<LoginProperties>
}