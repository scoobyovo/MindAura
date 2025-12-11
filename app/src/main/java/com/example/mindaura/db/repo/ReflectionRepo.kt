package com.example.mindaura.db.repo

import com.example.mindaura.model.Reflection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing [Reflection] objects in Firestore.
 * Handles creating, updating, deleting, and observing reflections for a specific user.
 *
 * @param db FirebaseFirestore instance used for database operations.
 */
class ReflectionRepo(private val db: FirebaseFirestore) {

    /**
     * Returns a reference to the user's reflections collection.
     *
     * @param userId The ID of the user.
     */
    private fun reflections(userId: String) = db.collection("users")
        .document(userId)
        .collection("reflections")

    /**
     * Provides a [Flow] of the user's reflections.
     * Emits updates in real time whenever the user's reflections change.
     *
     * @param userId The ID of the user to observe.
     * @return Flow emitting a list of [Reflection] objects.
     */
    fun getReflectionsFlow(userId: String?): Flow<List<Reflection>> = callbackFlow {
        if (userId.isNullOrEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val listener = reflections(userId)
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Reflection::class.java) ?: emptyList()
                trySend(list).isSuccess
            }

        awaitClose { listener.remove() }
    }

    /**
     * Saves a reflection to Firestore.
     * Creates a new document if the reflection ID is empty, otherwise updates the existing document.
     *
     * @param reflection The [Reflection] object to save.
     */
    suspend fun saveReflection(reflection: Reflection) {
        val userId = reflection.user_id ?: return
        val doc = if (reflection.id.isEmpty())
            reflections(userId).document()
        else
            reflections(userId).document(reflection.id)

        reflection.id = doc.id
        doc.set(reflection).await()
    }

    /**
     * Deletes a reflection by its ID for the specified user.
     *
     * @param userId The ID of the user.
     * @param reflectionId The ID of the reflection to delete.
     */
    suspend fun deleteReflection(userId: String?, reflectionId: String) {
        if (userId.isNullOrEmpty()) return
        reflections(userId).document(reflectionId).delete().await()
    }

    /**
     * Updates an existing reflection in Firestore.
     *
     * @param reflection The [Reflection] object to update.
     */
    suspend fun updateReflection(reflection: Reflection) {
        val userId = reflection.user_id ?: return
        reflections(userId).document(reflection.id).set(reflection).await()
    }

    /**
     * Retrieves a single reflection by ID.
     *
     * @param userId The ID of the user.
     * @param reflectionId The ID of the reflection to fetch.
     * @return The [Reflection] object if found, or null if not.
     */
    suspend fun getReflectionById(userId: String?, reflectionId: String): Reflection? {
        if (userId.isNullOrEmpty()) return null
        val snapshot = reflections(userId).document(reflectionId).get().await()
        return snapshot.toObject(Reflection::class.java)
    }
}
