package com.example.mindaura.repo

import com.example.mindaura.model.Reflection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReflectionRepo(private val db: FirebaseFirestore) {

    private val reflections = db.collection("reflections")

    fun getReflectionsFlow(userId: String): Flow<List<Reflection>> = callbackFlow {
        val listener = reflections
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Reflection::class.java) ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    suspend fun saveReflection(reflection: Reflection) {
        val doc = if (reflection.id.isEmpty())
            reflections.document()
        else
            reflections.document(reflection.id)

        reflection.id = doc.id
        doc.set(reflection).await()
    }

    suspend fun deleteReflection(reflectionId: String) {
        reflections.document(reflectionId).delete().await()
    }

    suspend fun updateReflection(reflection: Reflection) {
        reflections.document(reflection.id).set(reflection).await()
    }

    suspend fun getReflectionById(reflectionId: String): Reflection? {
        val snapshot = reflections.document(reflectionId).get().await()
        return snapshot.toObject(Reflection::class.java)
    }
}
