package com.example.mindaura.repository

import com.example.mindaura.model.JournalEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Repository for managing [JournalEntry] objects in Firestore.
 * Provides real-time updates, creation, and modification of journal entries for a specific user.
 *
 * @param db FirebaseFirestore instance used for database operations.
 */
class JournalRepo(private val db: FirebaseFirestore) {

    /**
     * Returns a reference to the user's journal entries collection.
     *
     * @param userId The ID of the user.
     */
    private fun journalEntries(userId: String) = db.collection("users")
        .document(userId)
        .collection("journalEntry")

    /**
     * Provides a [Flow] of all journal entries for the user.
     * Updates in real time when entries are added or modified.
     *
     * @param userId The ID of the user to observe.
     * @return Flow emitting a list of [JournalEntry] objects.
     */
    fun getEntriesFlow(userId: String?): Flow<List<JournalEntry>> = callbackFlow {
        if (userId.isNullOrEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val listener = journalEntries(userId)
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

    /**
     * Saves a journal entry to Firestore.
     * Creates a new document if the entry ID is empty, otherwise updates the existing document.
     *
     * @param entry The [JournalEntry] object to save.
     */
    suspend fun saveEntry(entry: JournalEntry) {
        val userId = entry.user_id ?: return
        val doc = if (entry.id.isEmpty())
            journalEntries(userId).document()
        else
            journalEntries(userId).document(entry.id)

        entry.id = doc.id
        doc.set(entry).await()
    }

    /**
     * Updates an existing journal entry in Firestore.
     *
     * @param entry The [JournalEntry] object to update.
     */
    suspend fun updateEntry(entry: JournalEntry) {
        val userId = entry.user_id ?: return
        journalEntries(userId).document(entry.id).set(entry).await()
    }

    /**
     * Retrieves a journal entry for a specific date as a [Flow].
     * Emits the entry if found, or null if no entry exists for that day.
     *
     * @param userId The ID of the user.
     * @param selectedDate The date to query, in milliseconds.
     * @return Flow emitting a single [JournalEntry] or null.
     */
    fun getEntryByDate(userId: String?, selectedDate: Long): Flow<JournalEntry?> = callbackFlow {
        if (userId.isNullOrEmpty()) {
            trySend(null).isSuccess
            close()
            return@callbackFlow
        }

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

        val listener = journalEntries(userId)
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

