package com.example.mindaura.screens

import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavController
import com.example.mindaura.AuthenticationViewModel
import com.example.mindaura.R
import com.example.mindaura.model.Activities
import com.example.mindaura.model.Mood
import com.example.mindaura.toDateString
import com.example.mindaura.toFullDateString
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.vm.JournalViewModel
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalEntryPage(
    modifier: Modifier = Modifier,
    date: Date,
    navController: NavController,
    journalVM: JournalViewModel
) {
    val activities = remember {
        mutableStateListOf(
            Activities("Chores", R.drawable.a_chores),
            Activities("Cooking", R.drawable.a_cook),
            Activities("Date", R.drawable.a_date),
            Activities("Family", R.drawable.a_family),
            Activities("Hobbies", R.drawable.a_hobbies),
            Activities("Love", R.drawable.a_love),
            Activities("Music", R.drawable.a_music),
            Activities("Party", R.drawable.a_party),
            Activities("Reading", R.drawable.a_reading),
            Activities("School", R.drawable.a_school),
            Activities("Self care", R.drawable.a_selfcare),
            Activities("Socialize", R.drawable.a_social),
            Activities("Sports", R.drawable.a_sports),
            Activities("Work", R.drawable.a_work),
            Activities("Workout", R.drawable.a_workout)
        )
    }

    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var reflectionText by remember { mutableStateOf("") }
    val gratitude = remember { mutableStateListOf("", "", "") }
    var notes by remember {mutableStateOf("")}
    val millisDate = date.time

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF7A5CBD)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            "Hello user! Today is ${millisDate.toFullDateString()}",
            style = MindAuraTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mood Section
        Text("How are you feeling today?", style = MindAuraTheme.typography.titleNormal)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            Mood.entries.forEach { mood ->
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (selectedMood == mood) {
                                Modifier.border(
                                    width = 3.dp,
                                    color = Color(0xFFBFA1FF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable { selectedMood = mood },
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = mood.iconRes),
                            contentDescription = mood.label,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            mood.label,
                            style = MindAuraTheme.typography.labelNormal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activities Section
        Text("What activities have you done today?", style = MindAuraTheme.typography.titleNormal)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            activities.forEachIndexed {index, activity ->
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activities[index].isSelected) Color(0xFFBFA1FF) else Color(0xFFF7F4FF))
                        .clickable {
                            val updatedActivity = activities[index].copy(isSelected = !activities[index].isSelected)
                            activities[index] = updatedActivity},
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = activity.iconRes),
                            contentDescription = activity.label,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            activity.label,
                            style = MindAuraTheme.typography.labelNormal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reflection Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "How was your day overall?",
                    style = MindAuraTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = reflectionText,
                    onValueChange = { reflectionText = it },
                    label = { Text("Write your reflection here") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Gratitude Section
        GratitudeSection(gratitude) { index, value ->
            gratitude[index] = value
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Note to self", style = MindAuraTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Write personal notes here…") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 6,
        )
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                journalVM.saveEntry(
                    mood = selectedMood,
                    activities = activities.filter { it.isSelected }.toList(),
                    reflection = reflectionText,
                    gratitude = gratitude.toList(),
                    notes = notes,
                    date = millisDate
                ) {
                    navController.navigate("home") {
                        popUpTo("journalEntryPage") { inclusive = true }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFAD93EE)
            )
        ) {
            Text(
                "Save Journal",
                style = MindAuraTheme.typography.labelNormal
            )
        }
    }
}

@Composable
fun GratitudeSection(
    gratitudeList: MutableList<String>,
    onGratitudeChange: (index: Int, value: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "What are 3 things you're grateful for today?",
                style = MindAuraTheme.typography.labelNormal
            )
            Spacer(modifier = Modifier.height(12.dp))
            for (i in 0..2) {
                OutlinedTextField(
                    value = gratitudeList.getOrElse(i) { "" },
                    onValueChange = { onGratitudeChange(i, it) },
                    label = { Text("Gratitude #${i + 1}") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}
