package com.example.mindaura.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindaura.toDayMillis
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.db.vm.JournalViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale


/**
 * Displays a calendar with week and month views.
 * Allows users to view and select dates, navigate to journal entries,
 * and see which days already have journal entries.
 *
 * @param modifier Modifier for customizing the layout.
 * @param navController Navigation controller for navigating to journal entries.
 * @param journalVM ViewModel providing journaled days data.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    navController: NavController,
    journalVM: JournalViewModel,
) {
    journalVM.loadAllJournaledDays()

    val uiState by journalVM.uiState.collectAsState()
    val journaledDays = uiState.journaledDays ?: emptyList()

    var selectedExistingEntryDate by remember { mutableStateOf<LocalDate?>(null) }

    var currentView by remember { mutableStateOf("WEEK") }
    val today = LocalDate.now()

    // Week setup
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val dayOfWeekHeaders = DayOfWeek.values().toList()

    // Month setup
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { YearMonth.now().minusMonths(12) }
    val endMonth = remember { YearMonth.now().plusMonths(12) }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        // ---- Header ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            MonthHeader(yearMonth = currentMonth)

            Switch(
                checked = currentView == "MONTH",
                onCheckedChange = { currentView = if (it) "MONTH" else "WEEK" },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFBFA1FF),
                    uncheckedThumbColor = Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Calendar Card ----
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                // Day headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayOfWeekHeaders.forEach { day ->
                        Text(
                            text = day.name.take(3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ---- Week View ----
                if (currentView == "WEEK") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDays.forEach { date ->
                            WeekDayBox(
                                day = date,
                                isToday = date == today,
                                hasEntry = journaledDays.contains(date.toDayMillis()),
                                navController = navController,
                                onDateSelected = { selectedExistingEntryDate = it }
                            )
                        }
                    }
                }
                // ---- Month View ----
                else {
                    HorizontalCalendar(
                        state = state,
                        dayContent = { day ->
                            WeekDayBox(
                                day = day.date,
                                isToday = day.date == today,
                                hasEntry = journaledDays.contains(day.date.toDayMillis()),
                                navController = navController,
                                onDateSelected = { selectedExistingEntryDate = it }
                            )
                        },
                        userScrollEnabled = true
                    )
                }
            }
        }
        selectedExistingEntryDate?.let { selectedDate ->
            ExistingEntryCard(selectedDate, navController)
        }
    }
}

/**
 * Represents a single day box in the calendar.
 * Highlights today and indicates if a journal entry exists.
 *
 * @param day The date represented by this box.
 * @param isToday Whether this day is today.
 * @param navController NavController for navigation.
 * @param hasEntry True if the day has a journal entry.
 * @param onDateSelected Callback when a day with an existing entry is selected.
 */
@Composable
fun WeekDayBox(
    day: LocalDate,
    isToday: Boolean = false,
    navController: NavController,
    hasEntry: Boolean = false,
    onDateSelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isToday) Color(0xFF395685) else Color(0xFFF7F4FF))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable {
                val millis = day.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (!hasEntry) {
                    navController.navigate("journalEntry/$millis")
                } else {
                    onDateSelected(day)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) Color.White else Color.DarkGray
        )

        if (hasEntry) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(12.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEF6868))
            )
        }
    }
}

/**
 * Displays the month and year at the top of the calendar.
 *
 * @param yearMonth The month and year to display.
 */
@Composable
fun MonthHeader(yearMonth: YearMonth) {
    Text(
        text = "${
            yearMonth.month.getDisplayName(
                java.time.format.TextStyle.FULL,
                Locale.getDefault()
            )
        } ${yearMonth.year}",
        style = MindAuraTheme.typography.titleNormal
    )
}

/**
 * Shows a card for days that already have a journal entry.
 * Can be extended to display entry details or allow editing.
 *
 * @param date The date of the existing journal entry.
 * @param navController NavController for navigation (currently unused).
 */
@Composable
fun ExistingEntryCard(date: LocalDate, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("You already journaled on this day!", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(date.toString())
            Spacer(modifier = Modifier.height(12.dp))
            Text("Visible soon")
        }
    }
}

