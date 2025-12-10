package com.example.mindaura.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindaura.model.Reflection

@Dao
interface ReflectionDao {
    @Query("SELECT * FROM quoteReflections")
    fun getAllReflections() : List<Reflection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReflection(reflection : Reflection)

    @Update
    fun updateReflection(reflection : Reflection)

    @Delete
    suspend fun delete(reflection : Reflection)
}