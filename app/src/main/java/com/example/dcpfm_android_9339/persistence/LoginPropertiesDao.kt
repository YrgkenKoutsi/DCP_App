package com.example.dcpfm_android_9339.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dcpfm_android_9339.models.LoginProperties

@Dao
interface LoginPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(loginProperties: LoginProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(loginProperties: LoginProperties): Long

    @Query("SELECT * FROM login_properties WHERE id = :id")
    suspend fun searchById(id: String): LiveData<LoginProperties>

    @Query("SELECT * FROM login_properties WHERE id = :id")
    suspend fun searchByUsername(id: String): LoginProperties?


}