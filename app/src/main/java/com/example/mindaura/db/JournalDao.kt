package com.example.mindaura.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindaura.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJournalEntry(journalEntry: JournalEntry)

    @Query("SELECT * FROM journalEntry WHERE date = :date")
    fun getJournalEntryByDate(date: Date): JournalEntry?

    @Update
    fun updateJournalEntry(journalEntry: JournalEntry)

    @Query("SELECT * FROM journalEntry")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    // More to add for stats?
}