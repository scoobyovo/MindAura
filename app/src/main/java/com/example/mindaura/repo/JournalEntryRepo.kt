package com.example.mindaura.repository

import com.example.mindaura.model.JournalEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class JournalRepo(private val db: FirebaseFirestore) {

    private val journalEntries = db.collection("journalEntry")

    fun getEntriesFlow(userId: String?): Flow<List<JournalEntry>> = callbackFlow {
        val listener = journalEntries
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(JournalEntry::class.java) ?: emptyList()
                trySend(list).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveEntry(entry: JournalEntry) {
        val doc = if (entry.id.isEmpty())
            journalEntries.document()
        else
            journalEntries.document(entry.id)

        entry.id = doc.id
        doc.set(entry).await()
    }

    suspend fun updateEntry(entry: JournalEntry) {
        journalEntries.document(entry.id).set(entry).await()
    }

    fun getEntryByDate(userId: String, selectedDate: Long): Flow<JournalEntry?> = callbackFlow {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        val listener = journalEntries
            .whereEqualTo("user_id", userId)
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThan("date", endOfDay)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects(JournalEntry::class.java) ?: emptyList()
                trySend(list.firstOrNull())
            }

        awaitClose { listener.remove() }
    }


}
