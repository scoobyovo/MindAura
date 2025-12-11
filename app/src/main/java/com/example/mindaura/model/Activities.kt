package com.example.mindaura.model

/**
 * Represents an activity a user can select in their journal entry.
 *
 * @property label The name of the activity.
 * @property iconRes Resource ID for the icon representing the activity.
 * @property isSelected Whether the activity is currently selected by the user.
 */
data class Activities(
    val label: String = "",
    val  iconRes: Int = 0,
    var isSelected: Boolean = false
)