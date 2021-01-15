package com.example.dcpfm_android_9339.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dcpfm_android_9339.models.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authToken: AuthToken): Long

    @Query("UPDATE login SET token = null WHERE account_id = :id")
    fun nullifyToken(id: Int): Int

    @Query("UPDATE login SET id = null WHERE account_id = :id")
    fun nullifyId(id: Int): Int

    @Query("SELECT * FROM login WHERE account_id = :id")
    suspend fun searchById(id: Int): AuthToken?
}