package com.example.mindaura.repository

import com.example.mindaura.db.JournalDao
import com.example.mindaura.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Manages all data operations for Journal Entries.
 * It uses the DAO to interact with the local Room database.
 */
class JournalEntryRepo(private val journalDao: JournalDao) {

    /**
     * Retrieves all journal entries from the database via the DAO.
     * Returns a Flow, which will automatically update the UI when data changes.
     */
    fun getEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllJournalEntries() // 3. Use the DAO to get data
    }

    /**
     * Inserts a new journal entry into the database via the DAO.
     */
    suspend fun insert(entry: JournalEntry) {
        journalDao.insertJournalEntry(entry) // 4. Use the DAO to insert data
    }

    // You can add other functions here that your ViewModel might need
    suspend fun update(entry: JournalEntry) {
        journalDao.updateJournalEntry(entry)
    }

    suspend fun getEntryByDate(date: Date): JournalEntry? {
        return journalDao.getJournalEntryByDate(date)
    }
}
