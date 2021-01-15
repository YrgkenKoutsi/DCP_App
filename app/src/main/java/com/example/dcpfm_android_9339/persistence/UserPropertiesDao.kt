package com.example.dcpfm_android_9339.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dcpfm_android_9339.models.UserProperties

@Dao
interface UserPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(userProperties: UserProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(userProperties: UserProperties): Long

    @Query("SELECT * FROM user_properties WHERE username = :username")
    suspend fun searchByUsername(username: String): UserProperties?

    @Query("SELECT * FROM user_properties WHERE id = :id")
    fun searchById(id: Int): UserProperties?
//    fun searchById(id: Int): LiveData<UserProperties>

}